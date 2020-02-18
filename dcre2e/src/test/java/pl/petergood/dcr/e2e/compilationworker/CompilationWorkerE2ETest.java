package pl.petergood.dcr.e2e.compilationworker;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.e2e.E2EApplication;
import pl.petergood.dcr.e2e.E2EConfig;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ContextConfiguration(classes = E2EApplication.class)
public class CompilationWorkerE2ETest {

    @Autowired
    private MessageProducer<ProcessingRequestMessage> processingRequestMessageProducer;

    @Autowired
    private MessageConsumer<ProcessingResultMessage> processingResultMessageConsumer;

    @Autowired
    private MessageConsumer<ProcessingFailureMessage> processingFailureMessageConsumer;

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

        Collection<ProcessingResultMessage> resultMessages = new LinkedBlockingDeque<>();
        processingResultMessageConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) processingResultMessageConsumer);
        t.start();

        // when
        processingRequestMessageProducer.publish(processingRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.size() == 1);
        List<ProcessingResultMessage> messages = new ArrayList<>(resultMessages);
        Assertions.assertThat(messages.get(0).getLanguageId()).isEqualTo("CPP");
        Assertions.assertThat(messages.get(0).getProcessedBytes().length).isGreaterThan(100);

        t.interrupt();
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

        Collection<ProcessingFailureMessage> resultMessages = new LinkedBlockingDeque<>();
        processingFailureMessageConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) processingFailureMessageConsumer);
        t.start();

        // when
        processingRequestMessageProducer.publish(processingRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> resultMessages.size() == 1);
        List<ProcessingFailureMessage> messages = new ArrayList<>(resultMessages);
        Assertions.assertThat(messages.get(0).getError()).contains("error: 'asdf' was not declared in this scope");

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

        Collection<ProcessingResultMessage> resultMessages = new LinkedBlockingDeque<>();
        processingResultMessageConsumer.setOnMessageReceived(resultMessages::addAll);
        Thread t = new Thread((Runnable) processingResultMessageConsumer);
        t.start();

        // when
        for (int i = 0; i < 20; i++) {
            processingRequestMessageProducer.publish(processingRequest);
            Thread.sleep(500);
        }

        // then
        Awaitility.await().atMost(Duration.ofMinutes(3)).until(() -> resultMessages.size() == 20);
        List<ProcessingResultMessage> messages = new ArrayList<>(resultMessages);

        Assertions.assertThat(messages).extracting(ProcessingResultMessage::getLanguageId).containsOnly("CPP");
        Assertions.assertThat(messages).extracting(ProcessingResultMessage::getProcessedBytes)
                .extracting((bytes) -> bytes.length).allSatisfy((len) -> Assertions.assertThat(len).isGreaterThan(100));


        t.interrupt();
    }
}
