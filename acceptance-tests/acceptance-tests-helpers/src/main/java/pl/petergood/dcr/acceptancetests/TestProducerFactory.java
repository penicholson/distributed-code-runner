package pl.petergood.dcr.acceptancetests;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;

import java.util.Properties;

public class TestProducerFactory {

    public static <T> MessageProducer<String, T> createProducer(String bootstrapServer, String topicName) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaMessageProducer<>(topicName, properties, new StringSerializer(), new ObjectSerializer<>());
    }

}
