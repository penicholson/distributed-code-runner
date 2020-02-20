package pl.petergood.dcr.runnerworker.simple;

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
        classes = SimpleRunnerWorkerApplication.class,
        initializers = SimpleRunnerWorkerApplicationTest.ContextInitializer.class
)
@EmbeddedKafka(partitions = 1, topics = { "simple-execution-request", "simple-execution-result" }, controlledShutdown = true)
public class SimpleRunnerWorkerApplicationTest {

    @Test
    public void contextLoads() {
    }

    public static class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    "dcr.simplerunnerworker.broker.bootstrap.urls=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}");
        }
    }

}
