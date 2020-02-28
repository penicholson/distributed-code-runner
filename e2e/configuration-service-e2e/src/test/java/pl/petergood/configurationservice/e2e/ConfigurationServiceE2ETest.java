package pl.petergood.configurationservice.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ContextConfiguration(classes = E2EConfigurationServiceApplication.class)
public class ConfigurationServiceE2ETest {

    @Autowired
    private E2EConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void verifyExecutionProfileIsSavedAndRetrieved() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setCpuTimeLimitSeconds(7);
        executionProfile.setMemoryLimitBytes(163);

        // when
        ResponseEntity<ExecutionProfile> responseEntity = restTemplate.postForEntity(configuration.getConfigurationServiceUrl() + "/executionprofile",
                executionProfile, ExecutionProfile.class);

        // then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().getCpuTimeLimitSeconds()).isEqualTo(7);
        Assertions.assertThat(responseEntity.getBody().getMemoryLimitBytes()).isEqualTo(163);
    }

    @Test
    public void verifyExecutionProfilesViolatingConstraintsAreNotPersisted() throws Exception {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setMemoryLimitBytes(-1);
        executionProfile.setCpuTimeLimitSeconds(10);

        // when
        Throwable thrownException = Assertions.catchThrowable(() -> {
            restTemplate.postForEntity(configuration.getConfigurationServiceUrl() + "/executionprofile",
                    executionProfile, Object.class);
        });

        // then
        Assertions.assertThat(thrownException).isInstanceOf(HttpClientErrorException.class);
        HttpClientErrorException exception = (HttpClientErrorException) thrownException;
        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        JsonNode errorBody = objectMapper.readTree(exception.getResponseBodyAsString());
        Assertions.assertThat(errorBody.get("message").asText()).isEqualTo("The given execution profile violates constraints");
    }

    @Test
    public void verifyQueryForNonExistentExecutionProfile() throws Exception {
        // given
        // when
        Throwable thrownException = Assertions.catchThrowable(() -> {
            restTemplate.getForEntity(configuration.getConfigurationServiceUrl() + "/executionprofile/123", Object.class);
        });

        // then
        Assertions.assertThat(thrownException).isInstanceOf(HttpClientErrorException.class);
        HttpClientErrorException exception = (HttpClientErrorException) thrownException;
        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        JsonNode errorBody = objectMapper.readTree(exception.getResponseBodyAsString());
        Assertions.assertThat(errorBody.get("message").asText()).isEqualTo("The given execution profile does not exists.");
    }
}
