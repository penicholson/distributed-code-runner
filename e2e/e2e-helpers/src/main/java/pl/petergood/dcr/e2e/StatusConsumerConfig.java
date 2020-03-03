package pl.petergood.dcr.e2e;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class StatusConsumerConfig {

    @Value("${dcr.e2e.kafka.bootstrap.urls:192.168.99.100:9092}")
    private String kafkaBootstrapUrls;

    @Value("${dcr.e2e.status.topic.name:status}")
    private String statusTopicName;

    @Bean
    public MessageConsumer<String, StatusMessage> statusConsumer() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrls);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        return new KafkaMessageConsumer<>(properties, statusTopicName, Duration.ofSeconds(1),
                new StringDeserializer(), new ObjectDeserializer<>(StatusMessage.class));
    }

}
