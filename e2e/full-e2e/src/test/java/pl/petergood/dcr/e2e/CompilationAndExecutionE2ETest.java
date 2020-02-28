package pl.petergood.dcr.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ForwardingType;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ContextConfiguration(classes = FullE2EApplication.class)
public class CompilationAndExecutionE2ETest {

    @Autowired
    private MessageProducer<ProcessingRequestMessage> processingRequestProducer;

    @Autowired
    private MessageConsumer<SimpleExecutionResultMessage> simpleExecutionResultConsumer;

    private Logger LOG = LoggerFactory.getLogger(CompilationAndExecutionE2ETest.class);

    @Value("${dcr.e2e.configurationservice.url}")
    private String configurationServiceUrl;

    private int executionProfileId;

    @BeforeEach
    public void createExecutionProfile() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String requestBody = "{\"cpuTimeLimitSeconds\":1,\"memoryLimitBytes\":1000000}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<String> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, new URI(configurationServiceUrl + "/executionprofile"));
        ResponseEntity<JsonNode> response = restTemplate.exchange(requestEntity, JsonNode.class);
        executionProfileId = response.getBody().get("id").asInt();
    }

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
        processingRequestMessage.setExecutionProfileId(executionProfileId);

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

    @Test
    public void verifyMultipleSimpleRequestsAreProcessed() throws Exception {
        // given
        String source = "#include <iostream>\n" +
                "#include <string>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\tstring s;\n" +
                "\tcin >> s;\n" +
                "\tcout << s << endl;\n" +
                "\treturn 0;\n" +
                "}";
        List<ProcessingRequestMessage> processingRequests = new ArrayList<>();
        Set<String> expectedResponses = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            ProcessingRequestMessage processingRequestMessage = new ProcessingRequestMessage("CPP", source);
            processingRequestMessage.setForwardingType(ForwardingType.SIMPLE);
            String uuid = UUID.randomUUID().toString();
            processingRequestMessage.setStdin(uuid);
            processingRequestMessage.setExecutionProfileId(executionProfileId);
            expectedResponses.add(uuid + "\n");
            processingRequests.add(processingRequestMessage);
        }

        Collection<SimpleExecutionResultMessage> executionResultMessages = new LinkedBlockingDeque<>();
        simpleExecutionResultConsumer.setOnMessageReceived((messages) -> {
            LOG.info("Got {} messages", messages.size());
            executionResultMessages.addAll(messages);
        });
        Thread t = new Thread((Runnable) simpleExecutionResultConsumer);
        t.start();

        // when
        for (ProcessingRequestMessage message : processingRequests) {
            processingRequestProducer.publish(message);
            Thread.sleep(200);
        }

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> executionResultMessages.size() == 20);
        Set<String> results = new HashSet<>();
        for (SimpleExecutionResultMessage message : executionResultMessages) {
            results.add(message.getStdout());
        }

        Assertions.assertThat(results).isEqualTo(expectedResponses);
    }
}
