package pl.petergood.dcr.configurationservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = { "pl.petergood.dcr.configurationservice.executionprofile" })
public class JpaConfiguration {



}
