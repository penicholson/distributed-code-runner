package pl.petergood.dcr.acceptancetests;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;

import java.time.Duration;
import java.util.Properties;

public class TestConsumerFactory {

    public static <T> MessageConsumer<String, T> createConsumer(Class<T> messageClass, String bootstrapServer, String consumerGroup, String topicName) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);

        return new KafkaMessageConsumer<>(properties, topicName, Duration.ofSeconds(1), new StringDeserializer(),
                new ObjectDeserializer<>(messageClass));
    }

}
