package pl.petergood.dcr.compilationworker;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import pl.petergood.dcr.acceptancetests.TestConsumerFactory;
import pl.petergood.dcr.acceptancetests.TestProducerFactory;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
@ContextConfiguration(
        classes = CompilationWorkerApplication.class,
        initializers = E2ECompilationWorkerAcceptanceTest.CompilationWorkerContextInitializer.class
)
@EmbeddedKafka(partitions = 1, topics = { "processing-request", "processing-result", "status" }, controlledShutdown = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class E2ECompilationWorkerAcceptanceTest {

    @Value("${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    private String brokerAddress;

    public static class CompilationWorkerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    "dcr.compilationworker.jail.configuration.kafka.bootstrap.urls=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
                    "dcr.compilationworker.jail.configuration.path=/test-nsjail.cfg",
                    "dcr.compilationworker.jail.root.path=/");
        }
    }

    @Test
    public void verifyCodeIsCompiled() {
        // given
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";

        MessageProducer<String, ProcessingRequestMessage> processingRequestMessageProducer = TestProducerFactory.createProducer(brokerAddress, "processing-request");
        MessageConsumer<String, ProcessingResultMessage> processingResultMessageConsumer = TestConsumerFactory.createConsumer(ProcessingResultMessage.class,
                brokerAddress, "test-group", "processing-result");
        MessageConsumer<String, StatusMessage> statusConsumer = TestConsumerFactory.createConsumer(StatusMessage.class,
                brokerAddress, "test-group", "status");

        Collection<ProcessingResultMessage> receivedMessages = new LinkedBlockingDeque<>();
        processingResultMessageConsumer.setOnMessageReceived((messages) -> receivedMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));
        Thread t = new Thread((Runnable) processingResultMessageConsumer);
        t.start();

        Collection<StatusMessage> receivedStatusMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived((messages) -> receivedStatusMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));
        Thread t2 = new Thread((Runnable) statusConsumer);
        t2.start();

        // when
        ProcessingRequestMessage processingRequestMessage = new ProcessingRequestMessage("CPP", source);
        processingRequestMessageProducer.publish("", processingRequestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(20)).until(() -> receivedMessages.size() == 1);
        List<ProcessingResultMessage> messages = new ArrayList<>(receivedMessages);
        Assertions.assertThat(messages.get(0).getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(messages.get(0).getProcessedBytes().length).isEqualTo(16944);

        Awaitility.await().atMost(Duration.ofSeconds(20)).until(() -> receivedStatusMessages.size() == 2);
        List<StatusMessage> statusMessages = new ArrayList<>(receivedStatusMessages);
        Assertions.assertThat(statusMessages.get(0).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_STARTED);
        Assertions.assertThat(statusMessages.get(1).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_SUCCESS);
    }

    @Test
    public void verifyIncorrectCodeIsNotCompiled() {
        // given
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\tasdf;\n" +
                "\treturn 0;\n" +
                "}";

        MessageProducer<String, ProcessingRequestMessage> processingRequestMessageProducer = TestProducerFactory.createProducer(brokerAddress, "processing-request");
        MessageConsumer<String, ProcessingResultMessage> processingResultMessageConsumer = TestConsumerFactory.createConsumer(ProcessingResultMessage.class,
                brokerAddress, "test-group", "processing-result");
        MessageConsumer<String, StatusMessage> statusConsumer = TestConsumerFactory.createConsumer(StatusMessage.class,
                brokerAddress, "test-group", "status");

        Collection<StatusMessage> receivedStatusMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived((messages) -> receivedStatusMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));
        Thread t = new Thread((Runnable) statusConsumer);
        t.start();

        AtomicInteger successMessageCount = new AtomicInteger();
        processingResultMessageConsumer.setOnMessageReceived((messages) -> successMessageCount.addAndGet(messages.size()));
        Thread t2 = new Thread((Runnable) processingResultMessageConsumer);
        t2.start();

        // when
        ProcessingRequestMessage processingRequestMessage = new ProcessingRequestMessage("CPP", source);
        processingRequestMessageProducer.publish("", processingRequestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(20)).until(() -> receivedStatusMessages.size() == 2);
        List<StatusMessage> messages = new ArrayList<>(receivedStatusMessages);
        Assertions.assertThat(messages.get(1).getMessage()).contains("error: 'asdf' was not declared in this scope");
        Assertions.assertThat(successMessageCount.get()).isEqualTo(0);
    }

}
