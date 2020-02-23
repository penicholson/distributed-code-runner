package pl.petergood.dcr.e2e.runnerworker.simple;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.e2e.SimpleRunnerWorkerE2EApplication;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ContextConfiguration(classes = SimpleRunnerWorkerE2EApplication.class)
public class SimpleRunnerWorkerE2ETest {

    @Autowired
    private MessageProducer<SimpleExecutionRequestMessage> requestMessageProducer;

    @Autowired
    private MessageConsumer<SimpleExecutionResultMessage> resultMessageConsumer;

    @Test
    public void verifyBinaryIsExecuted() throws Exception {
        // given
        byte[] bytes = Files.asByteSource(new File("testbinaries/sum")).read();
        SimpleExecutionRequestMessage requestMessage = new SimpleExecutionRequestMessage("CPP", bytes, "832040 1346269");

        Collection<SimpleExecutionResultMessage> receivedMessages = new LinkedBlockingDeque<>();
        resultMessageConsumer.setOnMessageReceived(receivedMessages::addAll);
        Thread t = new Thread((Runnable) resultMessageConsumer);
        t.start();

        // when
        requestMessageProducer.publish(requestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> receivedMessages.size() == 1);
        SimpleExecutionResultMessage message = receivedMessages.iterator().next();
        Assertions.assertThat(message.getExitCode()).isEqualTo(0);
        Assertions.assertThat(message.getStdout()).isEqualTo("2178309");
        Assertions.assertThat(message.getStderr()).isEqualTo("");
    }

}
