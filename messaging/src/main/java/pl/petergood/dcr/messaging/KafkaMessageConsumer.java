package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaMessageConsumer<K, V> implements MessageConsumer<K, V>, Runnable {

    private Consumer<K, V> consumer;
    private Duration pollingTimeout;
    private MessageReceivedEventHandler<K, V> eventHandler;

    public KafkaMessageConsumer(Properties properties, String topicName) {
        this(properties, topicName, Duration.ofMillis(500));
    }

    public KafkaMessageConsumer(Properties properties, String topicName, Duration pollingTimeout) {
        this(properties, topicName, pollingTimeout, null, null);
    }

    public KafkaMessageConsumer(Properties properties,
                                String topicName,
                                Duration pollingTimeout,
                                Deserializer<K> keyDeserializer,
                                Deserializer<V> valueDeserializer) {

        this.pollingTimeout = pollingTimeout;

        if (keyDeserializer != null && valueDeserializer != null) {
            consumer = new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
        } else {
            consumer = new KafkaConsumer<>(properties);
        }

        consumer.subscribe(Collections.singletonList(topicName));
    }

    @Override
    public void setOnMessageReceived(MessageReceivedEventHandler<K, V> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<K, V> polledRecords = consumer.poll(pollingTimeout);
            if (!polledRecords.isEmpty()) {
                eventHandler.handleMessageBatch(StreamSupport.stream(polledRecords.spliterator(), false)
                        .map((consumerRecord) -> new Message<>(consumerRecord.key(), consumerRecord.value()))
                        .collect(Collectors.toList()));

                consumer.commitSync();
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
