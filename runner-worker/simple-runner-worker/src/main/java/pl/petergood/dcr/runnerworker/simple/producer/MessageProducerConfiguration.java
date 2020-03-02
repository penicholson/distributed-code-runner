package pl.petergood.dcr.runnerworker.simple.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.messaging.KafkaMessageProducer;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;
import pl.petergood.dcr.runnerworker.simple.configuration.BrokerConfiguration;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class MessageProducerConfiguration {

    private BrokerConfiguration brokerConfiguration;
    private MessageProducer<String, SimpleExecutionResultMessage> resultMessageProducer;

    public MessageProducerConfiguration(BrokerConfiguration brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
    }

    @PostConstruct
    public void setupResultMessageProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getBrokerBootstrapUrls());
        resultMessageProducer = new KafkaMessageProducer<>(brokerConfiguration.getExecutionResultTopicName(), properties,
                new StringSerializer(), new ObjectSerializer<>());
    }

    public MessageProducer<String, SimpleExecutionResultMessage> getResultMessageProducer() {
        return resultMessageProducer;
    }
}
