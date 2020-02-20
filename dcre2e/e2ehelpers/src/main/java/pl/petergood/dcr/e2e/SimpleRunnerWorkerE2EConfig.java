package pl.petergood.dcr.e2e;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class SimpleRunnerWorkerE2EConfig {

    @Value("${dcr.e2e.kafka.bootstrap.urls:192.168.99.100:9092}")
    private String kafkaBootstrapUrls;

    @Value("${dcr.simplerunnerworker.request.topic.name:simple-execution-request}")
    private String executionRequestTopic;

    @Value("${dcr.simplerunnerworker.result.topic.name:simple-execution-result}")
    private String executionResultTopic;

    @Bean
    public MessageConsumer<SimpleExecutionResultMessage> executionResultConsumer() {
        Properties properties = createConsumerProperties("e2e-simple-execution-result");
        return new KafkaMessageConsumer<>(properties, executionResultTopic, Duration.ofSeconds(1),
                new StringDeserializer(), new ObjectDeserializer<>(SimpleExecutionResultMessage.class));
    }

    @Bean
    public MessageProducer<SimpleExecutionRequestMessage> executionRequestProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrls);
        return new KafkaMessageProducer<>(executionRequestTopic, properties,
                new StringSerializer(), new ObjectSerializer<>());
    }

    private Properties createConsumerProperties(String groupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrls);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }

}
