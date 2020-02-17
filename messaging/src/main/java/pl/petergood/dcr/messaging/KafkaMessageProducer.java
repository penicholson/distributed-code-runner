package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaMessageProducer<T> implements MessageProducer<T> {

    private Producer<String, T> producer;
    private String topicName;

    public KafkaMessageProducer(String topicName, Properties properties) {
        this.topicName = topicName;
        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void publish(T message) {
        ProducerRecord<String, T> record = new ProducerRecord<>(topicName, message);
        producer.send(record);

        // TODO: think about this
        producer.flush();
    }
}
