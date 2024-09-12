package pl.petergood.dcr.statusservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest
@ContextConfiguration(
        classes = StatusServiceApplication.class,
        initializers = StatusServiceApplicationTest.StatusServiceContextInitializer.class
)
@EmbeddedKafka(partitions = 1, topics = { "status" }, controlledShutdown = true)
public class StatusServiceApplicationTest {

    @Test
    public void contextLoads() {
    }

    public static class StatusServiceContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    "dcr.statusservice.kafka.bootstrap.urls=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}");
        }
    }

}
