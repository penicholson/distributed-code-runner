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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import pl.petergood.dcr.acceptancetests.TestConsumerFactory;
import pl.petergood.dcr.compilationworker.CompilationWorkerApplication;
import pl.petergood.dcr.compilationworker.configuration.JailConfiguration;
import pl.petergood.dcr.compilationworker.forwarder.ForwardingStrategyFactory;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.ForwardingType;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@SpringBootTest
@ContextConfiguration(
        initializers = ProcessingRequestEventHandlerAcceptanceTest.CompilationWorkerContextInitializer.class,
        classes = CompilationWorkerApplication.class
)
@TestPropertySource("/application.properties")
@EmbeddedKafka(partitions = 1, topics = { "processing-result", "status", "processing-failure" }, controlledShutdown = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    @Autowired
    private ForwardingStrategyFactory forwardingStrategyFactory;

    @Test
    public void verifyCompilationIsCompleted() {
        // given
        MessageConsumer<String, ProcessingResultMessage> consumer = TestConsumerFactory.createConsumer(ProcessingResultMessage.class, brokerAddress,
                "pr-test", "processing-result");
        Collection<Message<String, ProcessingResultMessage>> receivedMessages = new LinkedBlockingDeque<>();
        consumer.setOnMessageReceived(receivedMessages::addAll);
        Thread t = new Thread((Runnable) consumer);
        t.start();

        MessageConsumer<String, StatusMessage> statusConsumer = TestConsumerFactory.createConsumer(StatusMessage.class, brokerAddress,
                "status-test", "status");
        Collection<Message<String, StatusMessage>> receivedStatusMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived(receivedStatusMessages::addAll);
        Thread t2 = new Thread((Runnable) statusConsumer);
        t2.start();

        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";
        List<Message<String, ProcessingRequestMessage>> requests = Collections.singletonList(new Message<>("corelId", new ProcessingRequestMessage("CPP", source)));
        ProcessingRequestEventHandler eventHandler = new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor,
                fileInteractor, messageProducerConfiguration, forwardingStrategyFactory);

        // when
        eventHandler.handleMessageBatch(requests);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedMessages.size() == 1);
        List<ProcessingResultMessage> messages = receivedMessages.stream().map(Message::getMessage).collect(Collectors.toList());
        Assertions.assertThat(messages.get(0).getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(messages.get(0).getProcessedBytes().length).isEqualTo(16944);
        List<String> correlationIds = receivedMessages.stream().map(Message::getKey).collect(Collectors.toList());
        Assertions.assertThat(correlationIds).containsOnly("corelId");

        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedStatusMessages.size() == 2);
        List<StatusMessage> statusMessages = receivedStatusMessages.stream().map(Message::getMessage).collect(Collectors.toList());
        Assertions.assertThat(statusMessages.size()).isEqualTo(2);
        Assertions.assertThat(statusMessages.get(0).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_STARTED);
        Assertions.assertThat(statusMessages.get(1).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_SUCCESS);
        List<String> statusCorrelationIds = receivedStatusMessages.stream().map(Message::getKey).collect(Collectors.toList());
        Assertions.assertThat(statusCorrelationIds).containsOnly("corelId");
    }

    @Test
    public void verifyInvalidCodeIsNotCompiled() {
        // given
        MessageConsumer<String, StatusMessage> consumer = TestConsumerFactory.createConsumer(StatusMessage.class, brokerAddress,
                "status-test", "status");
        Collection<StatusMessage> receivedMessages = new LinkedBlockingDeque<>();
        consumer.setOnMessageReceived((messages) -> receivedMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));
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
        List<Message<String, ProcessingRequestMessage>> requests = Collections.singletonList(new Message<>("corelId", new ProcessingRequestMessage("CPP", source)));
        ProcessingRequestEventHandler eventHandler = new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor,
                fileInteractor, messageProducerConfiguration, forwardingStrategyFactory);

        // when
        eventHandler.handleMessageBatch(requests);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedMessages.size() == 2);
        List<StatusMessage> messages = new ArrayList<>(receivedMessages);

        List<StatusMessage> failureEvents = messages.stream().filter((message) -> message.getStatusEventType().equals(StatusEventType.PROCESSING_FAILURE))
                .collect(Collectors.toList());
        Assertions.assertThat(failureEvents.get(0).getMessage()).contains("error: 'asdf' was not declared in this scope");
    }

    @Test
    public void verifySimpleExecutionRequestIsCreated() {
        // given
        MessageConsumer<String, SimpleExecutionRequestMessage> consumer = TestConsumerFactory.createConsumer(SimpleExecutionRequestMessage.class, brokerAddress,
                "test-group", "simple-execution-request");
        Collection<Message<String, SimpleExecutionRequestMessage>> receivedMessages = new LinkedBlockingDeque<>();
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
        ProcessingRequestMessage processingRequestMessage = new ProcessingRequestMessage("CPP", source);
        processingRequestMessage.setForwardingType(ForwardingType.SIMPLE);
        processingRequestMessage.setStdin("hello world!");
        processingRequestMessage.setExecutionProfileId(3);
        ProcessingRequestEventHandler eventHandler = new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor,
                fileInteractor, messageProducerConfiguration, forwardingStrategyFactory);

        // when
        eventHandler.handleMessageBatch(Collections.singletonList(new Message<>("corelId", processingRequestMessage)));

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedMessages.size() == 1);
        SimpleExecutionRequestMessage executionRequest = receivedMessages.iterator().next().getMessage();
        String correlationId = receivedMessages.iterator().next().getKey();
        Assertions.assertThat(executionRequest.getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(executionRequest.getProcessedBytes().length).isEqualTo(16944);
        Assertions.assertThat(executionRequest.getStdin()).isEqualTo("hello world!");
        Assertions.assertThat(correlationId).isEqualTo(correlationId);
        Assertions.assertThat(executionRequest.getExecutionProfileId()).isEqualTo(3);
    }

}
