package pl.petergood.dcr.compilationworker.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.compilationworker.configuration.BrokerConfiguration;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Configuration
public class MessageProducerConfiguration {

    private BrokerConfiguration brokerConfiguration;

    private MessageProducer<ProcessingResultMessage> processingResultProducer;
    private MessageProducer<ProcessingFailureMessage> processingFailureProducer;

    public MessageProducerConfiguration(BrokerConfiguration brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
    }

    // TODO: what should we do if topic does not exist?
    @PostConstruct
    public void setupProcessingResultProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getKafkaBootstrapUrls());

        processingResultProducer = new KafkaMessageProducer<>(brokerConfiguration.getProcessingResultTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());

        processingFailureProducer = new KafkaMessageProducer<>(brokerConfiguration.getProcessingFailureTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());
    }

    @PreDestroy
    public void closeProducers() {
        processingResultProducer.close();
        processingFailureProducer.close();
    }

    public MessageProducer<ProcessingResultMessage> getProcessingResultProducer() {
        return processingResultProducer;
    }

    public MessageProducer<ProcessingFailureMessage> getProcessingFailureProducer() {
        return processingFailureProducer;
    }
}
