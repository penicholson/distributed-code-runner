package pl.petergood.dcr.compilationworker.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.petergood.dcr.compilationworker.configuration.BrokerConfiguration;
import pl.petergood.dcr.compilationworker.configuration.JailConfiguration;
import pl.petergood.dcr.compilationworker.forwarder.ForwardingStrategyFactory;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.shell.TerminalInteractor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Properties;

@Configuration
public class MessageConsumerConfiguration {

    private BrokerConfiguration brokerConfiguration;
    private ThreadPoolTaskExecutor taskExecutor;

    private JailConfiguration jailConfiguration;
    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;
    private MessageProducerConfiguration messageProducerConfiguration;
    private ForwardingStrategyFactory forwardingStrategyFactory;

    private MessageConsumer<ProcessingRequestMessage> messageConsumer;

    public MessageConsumerConfiguration(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                        BrokerConfiguration brokerConfiguration,
                                        JailConfiguration jailConfiguration,
                                        TerminalInteractor terminalInteractor,
                                        FileInteractor fileInteractor,
                                        MessageProducerConfiguration messageProducerConfiguration,
                                        ForwardingStrategyFactory forwardingStrategyFactory) {
        this.taskExecutor = threadPoolTaskExecutor;
        this.brokerConfiguration = brokerConfiguration;
        this.jailConfiguration = jailConfiguration;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
        this.messageProducerConfiguration = messageProducerConfiguration;
        this.forwardingStrategyFactory = forwardingStrategyFactory;
    }

    @PostConstruct
    public void setupMessageConsumers() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getKafkaBootstrapUrls());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, brokerConfiguration.getProcessingRequestConsumerGroup());
        messageConsumer = new KafkaMessageConsumer<>(properties, brokerConfiguration.getProcessingRequestTopicName(),
                Duration.ofSeconds(2), new StringDeserializer(), new ObjectDeserializer<>(ProcessingRequestMessage.class));
        messageConsumer.setOnMessageReceived(new ProcessingRequestEventHandler(jailConfiguration, terminalInteractor,
                fileInteractor, messageProducerConfiguration, forwardingStrategyFactory));

        taskExecutor.execute((Runnable) messageConsumer);
    }

    @PreDestroy
    public void closeConsumers() {
        messageConsumer.close();
    }

}
