package pl.petergood.dcr.e2e.compilationworker;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.e2e.CompilationWorkerE2EApplication;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ForwardingType;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@SpringBootTest
@ContextConfiguration(classes = CompilationWorkerE2EApplication.class)
public class CompilationWorkerE2ETest {

    @Autowired
    private MessageProducer<String, ProcessingRequestMessage> processingRequestMessageProducer;

    @Autowired
    private MessageConsumer<String, ProcessingResultMessage> processingResultMessageConsumer;

    @Autowired
    private MessageConsumer<String, SimpleExecutionRequestMessage> simpleExecutionRequestMessageConsumer;

    @Autowired
    private MessageConsumer<String, StatusMessage> statusConsumer;

    private Logger LOG = LoggerFactory.getLogger(CompilationWorkerE2ETest.class);

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
        ProcessingRequestMessage processingRequest = new ProcessingRequestMessage("CPP", source);

        Collection<Message<String, ProcessingResultMessage>> resultMessages = new LinkedBlockingDeque<>();
        processingResultMessageConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) processingResultMessageConsumer);
        t.start();

        Collection<Message<String, StatusMessage>> receivedStatusMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived(receivedStatusMessages::addAll);
        Thread t1 = new Thread((Runnable) statusConsumer);
        t1.start();

        // when
        processingRequestMessageProducer.publish("verifyCodeIsCompiled", processingRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.size() == 1);
        ProcessingResultMessage message = resultMessages.iterator().next().getMessage();
        Assertions.assertThat(message.getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(message.getProcessedBytes().length).isGreaterThan(100);
        Assertions.assertThat(resultMessages.iterator().next().getKey()).isEqualTo("verifyCodeIsCompiled");

        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> receivedStatusMessages.stream()
                .filter((msg) -> msg.getKey().equals("verifyCodeIsCompiled")).count() == 2);

        List<StatusMessage> statusMessages = receivedStatusMessages.stream()
                .filter((msg) -> msg.getKey().equals("verifyCodeIsCompiled"))
                .map(Message::getMessage)
                .collect(Collectors.toList());
        Assertions.assertThat(statusMessages.size()).isEqualTo(2);
        Assertions.assertThat(statusMessages.get(0).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_STARTED);
        Assertions.assertThat(statusMessages.get(1).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_SUCCESS);

        List<String> correlationIds = receivedStatusMessages.stream()
                .filter((msg) -> msg.getKey().equals("verifyCodeIsCompiled"))
                .map(Message::getKey)
                .collect(Collectors.toList());
        Assertions.assertThat(correlationIds).containsOnly("verifyCodeIsCompiled");

        t.interrupt();
        t1.interrupt();
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
        ProcessingRequestMessage processingRequest = new ProcessingRequestMessage("CPP", source);

        Collection<Message<String, StatusMessage>> resultMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) statusConsumer);
        t.start();

        // when
        processingRequestMessageProducer.publish("verifyIncorrectCodeIsNotCompiled", processingRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.stream()
                .filter((message) -> message.getKey().equals("verifyIncorrectCodeIsNotCompiled"))
                .count() == 2);

        List<StatusMessage> messages = resultMessages.stream()
                .filter((message) -> message.getKey().equals("verifyIncorrectCodeIsNotCompiled"))
                .map(Message::getMessage)
                .collect(Collectors.toList());
        Assertions.assertThat(messages.size()).isEqualTo(2);
        Assertions.assertThat(messages.get(1).getStatusEventType()).isEqualTo(StatusEventType.PROCESSING_FAILURE);
        Assertions.assertThat(messages.get(1).getMessage()).contains("error: 'asdf' was not declared in this scope");
        List<String> correlationIds = resultMessages.stream()
                .filter((message) -> message.getKey().equals("verifyIncorrectCodeIsNotCompiled"))
                .map(Message::getKey).collect(Collectors.toList());
        Assertions.assertThat(correlationIds).containsOnly("verifyIncorrectCodeIsNotCompiled");

        t.interrupt();
    }

    @Test
    public void verifySimpleExecutionRequestIsCreated() {
        // given
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";
        ProcessingRequestMessage processingRequest = new ProcessingRequestMessage("CPP", source);
        processingRequest.setForwardingType(ForwardingType.SIMPLE);
        processingRequest.setStdin("hello world!");
        processingRequest.setExecutionProfileId(2);

        Collection<Message<String, SimpleExecutionRequestMessage>> resultMessages = new LinkedBlockingDeque<>();
        simpleExecutionRequestMessageConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) simpleExecutionRequestMessageConsumer);
        t.start();

        // when
        processingRequestMessageProducer.publish("verifySimpleExecutionRequestIsCreated", processingRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.stream()
                .filter((message) -> message.getKey().equals("verifySimpleExecutionRequestIsCreated")).count() == 1);

        List<Message<String, SimpleExecutionRequestMessage>> messages = resultMessages.stream()
                .filter((message) -> message.getKey().equals("verifySimpleExecutionRequestIsCreated"))
                .collect(Collectors.toList());
        Assertions.assertThat(messages.size()).isEqualTo(1);
        SimpleExecutionRequestMessage requestMessage = messages.get(0).getMessage();
        Assertions.assertThat(requestMessage.getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(requestMessage.getProcessedBytes().length).isGreaterThan(100);
        Assertions.assertThat(requestMessage.getStdin()).isEqualTo("hello world!");
        Assertions.assertThat(messages.get(0).getKey()).isEqualTo("verifySimpleExecutionRequestIsCreated");
        Assertions.assertThat(requestMessage.getExecutionProfileId()).isEqualTo(2);

        t.interrupt();
    }

    @Test
    public void verifyMultipleRequestsAreProcessed() throws Exception {
        // given
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";
        ProcessingRequestMessage processingRequest = new ProcessingRequestMessage("CPP", source);

        Collection<Message<String, ProcessingResultMessage>> resultMessages = new LinkedBlockingDeque<>();
        processingResultMessageConsumer.setOnMessageReceived((messages) -> {
            LOG.info("Got {} messages with corlId={}", messages.size(), messages.stream().map(Message::getKey).collect(Collectors.joining(",")));
            resultMessages.addAll(messages);
        });
        Thread t = new Thread((Runnable) processingResultMessageConsumer);
        t.start();

        List<String> expectedCorrelationIds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            expectedCorrelationIds.add("verifyMultipleRequestsAreProcessed_" + i);
        }

        // when
        for (int i = 0; i < 20; i++) {
            processingRequestMessageProducer.publish("verifyMultipleRequestsAreProcessed_" + i, processingRequest);
            Thread.sleep(500);
        }

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.stream()
                .filter((message) -> expectedCorrelationIds.contains(message.getKey())).count() == 20);

        List<ProcessingResultMessage> messages = resultMessages.stream()
                .filter((message) -> expectedCorrelationIds.contains(message.getKey()))
                .map(Message::getMessage)
                .collect(Collectors.toList());

        Assertions.assertThat(messages.size()).isEqualTo(20);
        Assertions.assertThat(messages).extracting(ProcessingResultMessage::getLanguageId).containsOnly("CPP");
        Assertions.assertThat(messages).extracting(ProcessingResultMessage::getProcessedBytes)
                .extracting((bytes) -> bytes.length).allSatisfy((len) -> Assertions.assertThat(len).isGreaterThan(100));

        List<String> correlationIds = resultMessages.stream()
                .filter((message) -> expectedCorrelationIds.contains(message.getKey()))
                .map(Message::getKey)
                .collect(Collectors.toList());
        Assertions.assertThat(new HashSet<>(correlationIds)).isEqualTo(new HashSet<>(expectedCorrelationIds));

        t.interrupt();
    }
}
