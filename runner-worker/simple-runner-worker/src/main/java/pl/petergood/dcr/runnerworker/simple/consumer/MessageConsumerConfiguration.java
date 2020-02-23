package pl.petergood.dcr.runnerworker.simple.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.messaging.KafkaMessageConsumer;
import pl.petergood.dcr.messaging.MessageConsumer;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.runnerworker.simple.configuration.BrokerConfiguration;
import pl.petergood.dcr.runnerworker.simple.configuration.JailConfiguration;
import pl.petergood.dcr.runnerworker.simple.producer.MessageProducerConfiguration;
import pl.petergood.dcr.shell.TerminalInteractor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Properties;

@Configuration
public class MessageConsumerConfiguration {

    private BrokerConfiguration brokerConfiguration;
    private ThreadPoolTaskExecutor taskExecutor;
    private MessageConsumer<SimpleExecutionRequestMessage> executionRequestConsumer;

    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;
    private JailConfiguration jailConfiguration;
    private MessageProducerConfiguration messageProducerConfiguration;

    public MessageConsumerConfiguration(BrokerConfiguration brokerConfiguration,
                                        ThreadPoolTaskExecutor taskExecutor,
                                        TerminalInteractor terminalInteractor,
                                        FileInteractor fileInteractor,
                                        JailConfiguration jailConfiguration,
                                        MessageProducerConfiguration messageProducerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
        this.taskExecutor = taskExecutor;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
        this.jailConfiguration = jailConfiguration;
        this.messageProducerConfiguration = messageProducerConfiguration;
    }

    @PostConstruct
    public void setupSimpleProcessingRequestConsumer() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerConfiguration.getBrokerBootstrapUrls());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, brokerConfiguration.getGroupName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        executionRequestConsumer = new KafkaMessageConsumer<>(properties, brokerConfiguration.getExecutionRequestTopicName(),
                Duration.ofSeconds(1), new StringDeserializer(), new ObjectDeserializer<>(SimpleExecutionRequestMessage.class));

        executionRequestConsumer.setOnMessageReceived(new SimpleExecutionRequestHandler(terminalInteractor, fileInteractor,
                jailConfiguration, messageProducerConfiguration));
        taskExecutor.execute((Runnable) executionRequestConsumer);
    }

    @PreDestroy
    public void closeConsumer() {
        executionRequestConsumer.close();
    }

}
