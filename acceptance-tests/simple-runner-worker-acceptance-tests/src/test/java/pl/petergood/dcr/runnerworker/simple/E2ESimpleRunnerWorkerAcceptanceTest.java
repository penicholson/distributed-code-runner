package pl.petergood.dcr.runnerworker.simple;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import pl.petergood.dcr.acceptancetests.TestConsumerFactory;
import pl.petergood.dcr.acceptancetests.TestProducerFactory;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ContextConfiguration(
        classes = SimpleRunnerWorkerApplication.class,
        initializers = E2ESimpleRunnerWorkerAcceptanceTest.ContextInitializer.class
)
@EmbeddedKafka(partitions = 1, topics = { "simple-execution-request", "simple-execution-result" }, controlledShutdown = true)
public class E2ESimpleRunnerWorkerAcceptanceTest {

    @Value("${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS +"}")
    private String bootstrapUrls;

    @Autowired
    private WireMockServer wireMockServer;

    @AfterEach
    public void resetWireMock() {
        this.wireMockServer.resetAll();
    }

    public static class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            WireMockServer wireMockServer = new WireMockServer();
            wireMockServer.start();
            configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    "dcr.simplerunnerworker.broker.bootstrap.urls=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
                    "dcr.simplerunnerworker.jail.configuration.path=/test-nsjail.cfg",
                    "dcr.simplerunnerworker.jail.root.path=/",
                    "dcr.simplerunnerworker.configurationservice.url=http://localhost:" + wireMockServer.port());
        }
    }

    @Test
    public void verifyCodeIsExecuted() throws Exception {
        // given
        wireMockServer.stubFor(get("/executionprofile/1").willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"id\":1,\"cpuTimeLimitSeconds\":5,\"memoryLimitBytes\":1000000}")));

        File binaryFile = new File("/dcr/acceptance-tests/testbinaries/sum");
        byte[] bytes = Files.asByteSource(binaryFile).read();
        SimpleExecutionRequestMessage requestMessage = new SimpleExecutionRequestMessage("CPP", bytes, "121393 196418", 1); // 317811

        MessageProducer<String, SimpleExecutionRequestMessage> messageProducer = TestProducerFactory.createProducer(bootstrapUrls, "simple-execution-request");
        MessageConsumer<String, SimpleExecutionResultMessage> messageConsumer = TestConsumerFactory.createConsumer(SimpleExecutionResultMessage.class,
                bootstrapUrls, "test-simple-runner-worker", "simple-execution-result");
        Collection<SimpleExecutionResultMessage> receivedMessages = new LinkedBlockingDeque<>();
        messageConsumer.setOnMessageReceived((messages) -> receivedMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));

        Thread t = new Thread((Runnable) messageConsumer);
        t.start();

        // when
        messageProducer.publish("", requestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> receivedMessages.size() == 1);
        SimpleExecutionResultMessage message = receivedMessages.iterator().next();
        Assertions.assertThat(message.getExitCode()).isEqualTo(0);
        Assertions.assertThat(message.getStdout()).isEqualTo("317811");
        Assertions.assertThat(message.getStderr()).isEqualTo("");
    }

}
