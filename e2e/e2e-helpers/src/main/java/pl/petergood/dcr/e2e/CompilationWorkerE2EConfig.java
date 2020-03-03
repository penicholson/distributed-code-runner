package pl.petergood.dcr.e2e;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class CompilationWorkerE2EConfig {
    @Value("${dcr.e2e.kafka.bootstrap.urls:192.168.99.100:9092}")
    private String kafkaBootstrapUrls;

    @Value("${dcr.e2e.processing.request.topic.name:processing-request}")
    private String processingRequestTopicName;

    @Value("${dcr.e2e.processing.result.topic.name:processing-result}")
    private String processingResultTopicName;

    @Value("${dcr.e2e.simpleexecution.request.topic.name:simple-execution-request}")
    private String simpleExecutionRequestTopicName;

    @Bean
    public MessageProducer<String, ProcessingRequestMessage> createProcessingRequestProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrls);
        return new KafkaMessageProducer<>(processingRequestTopicName, properties, new StringSerializer(),
                new ObjectSerializer<>());
    }

    @Bean
    public MessageConsumer<String, ProcessingResultMessage> createProcessingResultConsumer() {
        return new KafkaMessageConsumer<>(createConsumerProperties("e2e-processing-result"), processingResultTopicName,
                Duration.ofSeconds(1), new StringDeserializer(), new ObjectDeserializer<>(ProcessingResultMessage.class));
    }

    @Bean
    public MessageConsumer<String, SimpleExecutionRequestMessage> createSimpleExecutionRequestConsumer() {
        return new KafkaMessageConsumer<>(createConsumerProperties("e2e-execution-request"), simpleExecutionRequestTopicName,
                Duration.ofSeconds(1), new StringDeserializer(), new ObjectDeserializer<>(SimpleExecutionRequestMessage.class));
    }

    private Properties createConsumerProperties(String groupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrls);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }
}
