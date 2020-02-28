package pl.petergood.dcr.configurationservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

public class RestConfigurationServiceClient implements ConfigurationServiceClient {

    private RestTemplate restTemplate;

    public static final String EXECUTION_PROFILE_ENDPOINT = "/executionprofile";

    public RestConfigurationServiceClient(String configurationServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        UriTemplateHandler templateHandler = new DefaultUriBuilderFactory(configurationServiceUrl);
        restTemplate.setUriTemplateHandler(templateHandler);
    }

    public ExecutionProfile getExecutionProfile(int id) {
        ResponseEntity<ExecutionProfile> responseEntity = restTemplate.getForEntity(EXECUTION_PROFILE_ENDPOINT + "/" + id, ExecutionProfile.class);
        return responseEntity.getBody();
    }
}
