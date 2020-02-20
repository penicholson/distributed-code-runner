package pl.petergood.dcr.e2e;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ForwardingType;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ContextConfiguration(classes = FullE2EApplication.class)
public class CompilationAndExecutionE2ETest {

    @Autowired
    private MessageProducer<ProcessingRequestMessage> processingRequestProducer;

    @Autowired
    private MessageConsumer<SimpleExecutionResultMessage> simpleExecutionResultConsumer;

    @Autowired
    private MessageConsumer<SimpleExecutionRequestMessage> simpleExecutionRequestConsumer;

    private Logger LOG = LoggerFactory.getLogger(CompilationAndExecutionE2ETest.class);

    @Test
    public void verifySourceIsCompiledAndExecuted() {
        // given
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "    int a, b;\n" +
                "    cin >> a >> b;\n" +
                "    cout << \"The number is:\" << endl;\n" +
                "    cout << a + b;\n" +
                "    return 0;\n" +
                "}\n";
        ProcessingRequestMessage processingRequestMessage = new ProcessingRequestMessage("CPP", source);
        processingRequestMessage.setForwardingType(ForwardingType.SIMPLE);
        processingRequestMessage.setStdin("10946 17711"); // 28657

        Collection<SimpleExecutionResultMessage> executionResultMessages = new LinkedBlockingDeque<>();
        simpleExecutionResultConsumer.setOnMessageReceived(executionResultMessages::addAll);
        Thread t = new Thread((Runnable) simpleExecutionResultConsumer);
        t.start();

        // when
        processingRequestProducer.publish(processingRequestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> executionResultMessages.size() == 1);
        SimpleExecutionResultMessage resultMessage = executionResultMessages.iterator().next();
        Assertions.assertThat(resultMessage.getExitCode()).isEqualTo(0);
        Assertions.assertThat(resultMessage.getStdout()).isEqualTo("The number is:\n28657");
        Assertions.assertThat(resultMessage.getStderr()).isEqualTo("");

        t.interrupt();
    }

}
