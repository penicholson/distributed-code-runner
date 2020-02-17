package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KafkaMessageProducerTest {

    @Rule
    public EmbeddedKafkaRule kafkaRule = new EmbeddedKafkaRule(1, true, "test-topic");

    private Consumer<String, String> consumer;

    @Before
    public void setupConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaRule.getEmbeddedKafka().getBrokersAsString());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("test-topic"));
    }

    @Test
    public void verifyMessageIsProduced() {
        // given
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaRule.getEmbeddedKafka().getBrokersAsString());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        MessageProducer<String> messageProducer = new KafkaMessageProducer<>("test-topic", properties);

        // when
        messageProducer.publish("hello world!");

        // then
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, 1000);
        Assertions.assertThat(records.count()).isEqualTo(1);

        ConsumerRecord<String, String> record = StreamSupport.stream(records.spliterator(), false).collect(Collectors.toList()).get(0);
        Assertions.assertThat(record.value()).isEqualTo("hello world!");
    }

}
