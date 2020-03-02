package pl.petergood.dcr.compilationworker.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.compilationworker.configuration.BrokerConfiguration;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;
import pl.petergood.dcr.messaging.status.StatusMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Configuration
public class MessageProducerConfiguration {

    private BrokerConfiguration brokerConfiguration;

    private MessageProducer<String, ProcessingResultMessage> processingResultProducer;
    private MessageProducer<String, SimpleExecutionRequestMessage> simpleExecutionRequestProducer;

    private MessageProducer<String, StatusMessage> statusProducer;

    public MessageProducerConfiguration(BrokerConfiguration brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
    }

    // TODO: what should we do if topic does not exist?
    @PostConstruct
    public void setupProducers() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getKafkaBootstrapUrls());

        processingResultProducer = new KafkaMessageProducer<>(brokerConfiguration.getProcessingResultTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());

        simpleExecutionRequestProducer = new KafkaMessageProducer<>(brokerConfiguration.getSimpleExecutionRequestTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());

        statusProducer = new KafkaMessageProducer<>(brokerConfiguration.getStatusTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());
    }

    @PreDestroy
    public void closeProducers() {
        processingResultProducer.close();
        simpleExecutionRequestProducer.close();
        statusProducer.close();
    }

    public MessageProducer<String, ProcessingResultMessage> getProcessingResultProducer() {
        return processingResultProducer;
    }

    public MessageProducer<String, SimpleExecutionRequestMessage> getSimpleExecutionRequestProducer() {
        return simpleExecutionRequestProducer;
    }

    public MessageProducer<String, StatusMessage> getStatusProducer() {
        return statusProducer;
    }
}
