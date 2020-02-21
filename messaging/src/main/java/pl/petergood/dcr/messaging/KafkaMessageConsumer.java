package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaMessageConsumer<T> implements MessageConsumer<T>, Runnable {

    // TODO: should we use String key?
    private Consumer<String, T> consumer;
    private Duration pollingTimeout;
    private MessageReceivedEventHandler<T> eventHandler;
    private EventHandlerThreadPoolExecutor<T> threadPoolExecutor;
    private boolean isActive = true;

    private Logger LOG = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    public KafkaMessageConsumer(Properties properties, String topicName) {
        this(properties, topicName, Duration.ofMillis(500));
    }

    public KafkaMessageConsumer(Properties properties, String topicName, Duration pollingTimeout) {
        this(properties, topicName, pollingTimeout, null, null);
    }

    public KafkaMessageConsumer(Properties properties,
                                String topicName,
                                Duration pollingTimeout,
                                Deserializer<String> keyDeserializer,
                                Deserializer<T> valueDeserializer) {

        this.pollingTimeout = pollingTimeout;
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

        if (keyDeserializer != null && valueDeserializer != null) {
            consumer = new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
        } else {
            consumer = new KafkaConsumer<>(properties);
        }

        consumer.subscribe(Collections.singletonList(topicName));
        threadPoolExecutor = new EventHandlerThreadPoolExecutor<>(consumer, 1, 1, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    public void setOnMessageReceived(MessageReceivedEventHandler<T> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        while (isActive) {
            try {
                ConsumerRecords<String, T> polledRecords = consumer.poll(pollingTimeout);
                if (!polledRecords.isEmpty()) {
                    eventHandler.setMessagesToProcess(StreamSupport.stream(polledRecords.spliterator(), false)
                            .map(ConsumerRecord::value)
                            .collect(Collectors.toList()));

                    threadPoolExecutor.execute(eventHandler);
                    consumer.pause(consumer.assignment());
                }

                if (threadPoolExecutor.getActiveCount() == 0) {
                    consumer.commitSync();
                    consumer.resume(consumer.assignment());
                    LOG.info("Resumed consumer");
                }
            } finally {
                consumer.close();
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
