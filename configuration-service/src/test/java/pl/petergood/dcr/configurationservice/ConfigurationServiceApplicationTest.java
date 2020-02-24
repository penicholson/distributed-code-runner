package pl.petergood.dcr.configurationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ConfigurationServiceApplication.class)
public class ConfigurationServiceApplicationTest {
    @Test
    public void verifyContextLoads() {
    }
}
