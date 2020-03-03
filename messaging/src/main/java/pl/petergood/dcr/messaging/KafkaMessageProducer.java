package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Properties;

public class KafkaMessageProducer<K, T> implements MessageProducer<K, T> {

    private Producer<K, T> producer;
    private String topicName;

    public KafkaMessageProducer(String topicName, Properties properties) {
        this.topicName = topicName;
        this.producer = new KafkaProducer<>(properties);
    }

    public KafkaMessageProducer(String topicName,
                                Properties properties,
                                Serializer<K> keySerializer,
                                Serializer<T> valueSerializer) {
        this.topicName = topicName;
        this.producer = new KafkaProducer<>(properties, keySerializer, valueSerializer);
    }

    @Override
    public void publish(K key, T message) {
        ProducerRecord<K, T> record = new ProducerRecord<>(topicName, key, message);
        producer.send(record);

        // TODO: think about this
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}
