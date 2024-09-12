package pl.petergood.dcr.statusservice.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.status.StatusMessage;
import pl.petergood.dcr.statusservice.configuration.BrokerConfiguration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Properties;

@Configuration
public class StatusConsumerConfiguration {

    private BrokerConfiguration brokerConfiguration;
    private TaskExecutor taskExecutor;

    private MessageConsumer<String, StatusMessage> statusConsumer;

    public StatusConsumerConfiguration(BrokerConfiguration brokerConfiguration,
                                       TaskExecutor taskExecutor) {
        this.brokerConfiguration = brokerConfiguration;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void setupStatusConsumer() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getBootstrapUrls());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, brokerConfiguration.getConsumerGroupName());
        statusConsumer = new KafkaMessageConsumer<>(properties, brokerConfiguration.getStatusTopicName(),
                Duration.ofSeconds(3), new StringDeserializer(), new ObjectDeserializer<>(StatusMessage.class));
        statusConsumer.setOnMessageReceived(new StatusReceivedEventHandler());

        taskExecutor.execute((Runnable) statusConsumer);
    }

    public MessageConsumer<String, StatusMessage> getStatusConsumer() {
        return statusConsumer;
    }
}
