package pl.petergood.dcr.e2e.runnerworker.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
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
import pl.petergood.dcr.e2e.SimpleRunnerWorkerE2EApplication;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@SpringBootTest
@ContextConfiguration(classes = SimpleRunnerWorkerE2EApplication.class)
public class SimpleRunnerWorkerE2ETest {

    @Autowired
    private MessageProducer<String, SimpleExecutionRequestMessage> requestMessageProducer;

    @Autowired
    private MessageConsumer<String, SimpleExecutionResultMessage> resultMessageConsumer;

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
    public void verifyBinaryIsExecuted() throws Exception {
        // given
        byte[] bytes = Files.asByteSource(new File("testbinaries/sum")).read();
        SimpleExecutionRequestMessage requestMessage = new SimpleExecutionRequestMessage("CPP", bytes, "832040 1346269", executionProfileId);

        Collection<SimpleExecutionResultMessage> receivedMessages = new LinkedBlockingDeque<>();
        resultMessageConsumer.setOnMessageReceived((messages) -> receivedMessages.addAll(messages.stream().map(Message::getMessage).collect(Collectors.toList())));
        Thread t = new Thread((Runnable) resultMessageConsumer);
        t.start();

        Collection<Message<String, StatusMessage>> receivedStatusMessages = new LinkedBlockingDeque<>();
        statusConsumer.setOnMessageReceived(receivedStatusMessages::addAll);
        Thread t2 = new Thread((Runnable) statusConsumer);
        t2.start();

        // when
        requestMessageProducer.publish("verifyBinaryIsExecuted", requestMessage);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> receivedMessages.size() == 1);
        SimpleExecutionResultMessage message = receivedMessages.iterator().next();
        Assertions.assertThat(message.getExitCode()).isEqualTo(0);
        Assertions.assertThat(message.getStdout()).isEqualTo("2178309");
        Assertions.assertThat(message.getStderr()).isEqualTo("");

        Awaitility.await().atMost(Duration.ofSeconds(30)).until(() -> receivedStatusMessages.stream()
            .filter((msg) -> msg.getKey().equals("verifyBinaryIsExecuted"))
            .count() == 2);
        List<Message<String, StatusMessage>> statusMessages = receivedStatusMessages.stream()
                .filter((msg) -> msg.getKey().equals("verifyBinaryIsExecuted"))
                .collect(Collectors.toList());
        Assertions.assertThat(statusMessages.get(0).getMessage().getStatusEventType()).isEqualTo(StatusEventType.RUN_STARTED);
        Assertions.assertThat(statusMessages.get(1).getMessage().getStatusEventType()).isEqualTo(StatusEventType.RUN_FINISHED);
    }

}
