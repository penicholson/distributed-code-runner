package pl.petergood.dcr.compilationworker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest
@ContextConfiguration(
        initializers = CompilationWorkerApplicationTest.CompilationWorkerContextInitializer.class,
        classes = CompilationWorkerApplication.class
)
@TestPropertySource("/application.properties")
@EmbeddedKafka(partitions = 1, topics = { "processing-request", "processing-result", "processing-failure" }, controlledShutdown = true)
public class CompilationWorkerApplicationTest {

    @Test
    public void verifyContextLoads() {
    }

    public static class CompilationWorkerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    "dcr.compilationworker.jail.configuration.kafka.bootstrap.urls=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}");
        }
    }

}
