package pl.petergood.configurationservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.configurationservice.ConfigurationServiceApplication;
import pl.petergood.dcr.configurationservice.executionprofile.ExecutionProfile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ConfigurationServiceApplication.class)
public class ConfigurationServiceAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void verifyInsertionAndRetrievalOfExecutionProfile() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setMemoryLimitBytes(100);
        executionProfile.setCpuTimeLimitSeconds(-2);

        // when
        ResponseEntity<ExecutionProfile> response = restTemplate.postForEntity("http://localhost:" + port + "/executionprofile",
                executionProfile, ExecutionProfile.class);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getMemoryLimitBytes()).isEqualTo(100);
        Assertions.assertThat(response.getBody().getCpuTimeLimitSeconds()).isEqualTo(2);

        // when
        ResponseEntity<ExecutionProfile> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/executionprofile/" + response.getBody().getId(),
                ExecutionProfile.class);

        // then
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody().getCpuTimeLimitSeconds()).isEqualTo(2);
        Assertions.assertThat(getResponse.getBody().getMemoryLimitBytes()).isEqualTo(100);
    }

}
