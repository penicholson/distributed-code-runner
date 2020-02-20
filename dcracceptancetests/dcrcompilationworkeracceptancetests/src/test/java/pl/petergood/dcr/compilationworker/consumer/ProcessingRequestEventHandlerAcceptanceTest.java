package pl.petergood.dcr.compilationworker.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import pl.petergood.dcr.compilationworker.CompilationWorkerApplication;
import pl.petergood.dcr.compilationworker.configuration.JailConfiguration;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ContextConfiguration(
        initializers = ProcessingRequestEventHandlerAcceptanceTest.CompilationWorkerContextInitializer.class,
        classes = CompilationWorkerApplication.class
)
@TestPropertySource("/application.properties")
@EmbeddedKafka(partitions = 1, topics = { "processing-result", "processing-failure" }, controlledShutdown = true)
public class ProcessingRequestEventHandlerAcceptanceTest {

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

    @Autowired
    private JailConfiguration jailConfiguration;

    @Autowired
    private TerminalInteractor terminalInteractor;

    @Autowired
    private FileInteractor fileInteractor;

    @Autowired
    private MessageProducerConfiguration messageProducerConfiguration;

    @Test
    public void verifyCompilationIsCompleted() {
        // given
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        MessageConsumer<ProcessingResultMessage> consumer = new KafkaMessageConsumer<>(properties, "processing-result", Duration.ofSeconds(1),
                new StringDeserializer(), new ObjectDeserializer<>(ProcessingResultMessage.class));
        Collection<ProcessingResultMessage> receivedMessages = new LinkedBlockingDeque<>();
        consumer.setOnMessageReceived(receivedMessages::addAll);
        Thread t = new Thread((Runnable) consumer);
        t.start();

        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";
        List<ProcessingRequestMessage> requests = Collections.singletonList(new ProcessingRequestMessage("CPP", source));
        ProcessingRequestEventHandler eventHandler = new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor, fileInteractor, messageProducerConfiguration);

        // when
        eventHandler.handleMessageBatch(requests);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedMessages.size() == 1);
        List<ProcessingResultMessage> messages = new ArrayList<>(receivedMessages);
        Assertions.assertThat(messages.get(0).getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(messages.get(0).getProcessedBytes().length).isEqualTo(16944);
    }

    @Test
    public void verifyInvalidCodeIsNotCompiled() {
        // given
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        MessageConsumer<ProcessingFailureMessage> consumer = new KafkaMessageConsumer<>(properties, "processing-failure", Duration.ofSeconds(1),
                new StringDeserializer(), new ObjectDeserializer<>(ProcessingFailureMessage.class));
        Collection<ProcessingFailureMessage> receivedMessages = new LinkedBlockingDeque<>();
        consumer.setOnMessageReceived(receivedMessages::addAll);
        Thread t = new Thread((Runnable) consumer);
        t.start();

        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\tasdf;\n" +
                "\treturn 0;\n" +
                "}";
        List<ProcessingRequestMessage> requests = Collections.singletonList(new ProcessingRequestMessage("CPP", source));
        ProcessingRequestEventHandler eventHandler = new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor, fileInteractor, messageProducerConfiguration);

        // when
        eventHandler.handleMessageBatch(requests);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedMessages.size() == 1);
        List<ProcessingFailureMessage> messages = new ArrayList<>(receivedMessages);
        Assertions.assertThat(messages.get(0).getError()).contains("error: 'asdf' was not declared in this scope");
    }

}
