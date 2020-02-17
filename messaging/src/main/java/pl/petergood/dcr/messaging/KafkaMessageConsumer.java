package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaMessageConsumer<T> implements MessageConsumer<T>, Runnable {

    private Consumer<String, T> consumer;
    private Duration pollingTimeout;
    private MessageReceivedEventHandler<T> eventHandler;

    public KafkaMessageConsumer(Properties properties, String topicName) {
        this(properties, topicName, Duration.ofMillis(500));
    }

    public KafkaMessageConsumer(Properties properties, String topicName, Duration pollingTimeout) {
        this.pollingTimeout = pollingTimeout;
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topicName));
    }

    @Override
    public void setOnMessageReceived(MessageReceivedEventHandler<T> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, T> polledRecords = consumer.poll(pollingTimeout);
            eventHandler.handleMessageBatch(StreamSupport.stream(polledRecords.spliterator(), false)
                .map(ConsumerRecord::value)
                .collect(Collectors.toList()));

            // TODO: think about this...
            consumer.commitSync();
        }
    }
}
