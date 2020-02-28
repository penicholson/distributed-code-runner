package pl.petergood.dcr.configurationservice.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RestConfigurationServiceClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void verifyConfigurationServiceIsCalled() {
        // given
        stubFor(get("/executionprofile/1").willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"id\":1,\"cpuTimeLimitSeconds\":7,\"memoryLimitBytes\":123}")));
        ConfigurationServiceClient configurationServiceClient = new RestConfigurationServiceClient("http://localhost:" + wireMockRule.port());

        // when
        ExecutionProfile executionProfile = configurationServiceClient.getExecutionProfile(1);

        // then
        Assertions.assertThat(executionProfile.getCpuTimeLimitSeconds()).isEqualTo(7);
        Assertions.assertThat(executionProfile.getMemoryLimitBytes()).isEqualTo(123);
    }

    @Test(expected = ExecutionProfileNotFoundException.class)
    public void verifyExceptionIsThrownWhenExecutionProfileNotFound() {
        // given
        stubFor(get("/executionprofile/1").willReturn(aResponse()
            .withStatus(404)
            .withBody("{}")));
        ConfigurationServiceClient configurationServiceClient = new RestConfigurationServiceClient("http://localhost:" + wireMockRule.port());

        // when
        ExecutionProfile executionProfile = configurationServiceClient.getExecutionProfile(1);
    }

}
