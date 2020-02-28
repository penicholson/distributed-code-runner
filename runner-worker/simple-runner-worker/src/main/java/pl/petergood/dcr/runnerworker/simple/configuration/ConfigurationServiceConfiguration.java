package pl.petergood.dcr.runnerworker.simple.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationServiceConfiguration {

    @Value("${dcr.simplerunnerworker.configurationservice.url}")
    private String configurationServiceUrl;

    public String getConfigurationServiceUrl() {
        return configurationServiceUrl;
    }
}
