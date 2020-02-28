package pl.petergood.configurationservice.e2e;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class E2EConfiguration {

    @Value("${dcr.e2e.configurationservice.url}")
    public String configurationServiceUrl;

    public String getConfigurationServiceUrl() {
        return configurationServiceUrl;
    }
}
