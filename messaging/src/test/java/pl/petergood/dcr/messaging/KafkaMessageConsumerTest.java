package pl.petergood.dcr.messaging;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;

public class KafkaMessageConsumerTest {

    @Rule
    public EmbeddedKafkaRule kafkaRule = new EmbeddedKafkaRule(1, false, "test-topic");

    private Producer<String, String> producer;

    @Before
    public void setupProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaRule.getEmbeddedKafka().getBrokersAsString());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    @Test
    public void verifyConsumerGetsMessages() {
        // given
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaRule.getEmbeddedKafka().getBrokersAsString());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        MessageConsumer<String> messageConsumer = new KafkaMessageConsumer<>(properties, "test-topic");
        Collection<String> messages = new LinkedBlockingDeque<>();

        messageConsumer.setOnMessageReceived(messages::addAll);

        // when
        Thread t = new Thread((Runnable) messageConsumer);
        t.start();

        producer.send(new ProducerRecord<>("test-topic", "hello world!"));

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10)).until(() -> messages.size() == 1);
        Assertions.assertThat(messages.size()).isEqualTo(1);
        Assertions.assertThat(messages.contains("hello world!")).isTrue();
    }

}
