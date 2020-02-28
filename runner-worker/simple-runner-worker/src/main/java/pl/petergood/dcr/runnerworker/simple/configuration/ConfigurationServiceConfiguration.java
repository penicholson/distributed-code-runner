package pl.petergood.dcr.runnerworker.simple.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.configurationservice.client.ConfigurationServiceClient;
import pl.petergood.dcr.configurationservice.client.RestConfigurationServiceClient;

@Configuration
public class ConfigurationServiceConfiguration {

    @Value("${dcr.simplerunnerworker.configurationservice.url}")
    private String configurationServiceUrl;

    public String getConfigurationServiceUrl() {
        return configurationServiceUrl;
    }

    @Bean
    public ConfigurationServiceClient configurationServiceClient() {
        return new RestConfigurationServiceClient(configurationServiceUrl);
    }
}
