package pl.petergood.dcr.compilationworker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {

    @Value("${dcr.compilationworker.jail.configuration.kafka.bootstrap.urls}")
    private String kafkaBootstrapUrls;

    @Value("${dcr.compilationworker.jail.configuration.kafka.processing.request.topic.name}")
    private String processingRequestTopicName;

    @Value("${dcr.compilationworker.jail.configuration.kafka.processing.request.consumer.group}")
    private String processingRequestConsumerGroup;

    @Value("${dcr.compilationworker.jail.configuration.kafka.processing.result.topic.name}")
    private String processingResultTopicName;

    @Value("${dcr.compilationworker.jail.configuration.kafka.processing.failure.topic.name}")
    private String processingFailureTopicName;

    @Value("${dcr.compilationworker.jail.configuration.kafka.simple.execution.topic.name}")
    private String simpleExecutionRequestTopicName;

    public String getKafkaBootstrapUrls() {
        return kafkaBootstrapUrls;
    }

    public String getProcessingRequestTopicName() {
        return processingRequestTopicName;
    }

    public String getProcessingRequestConsumerGroup() {
        return processingRequestConsumerGroup;
    }

    public String getProcessingResultTopicName() {
        return processingResultTopicName;
    }

    public String getProcessingFailureTopicName() {
        return processingFailureTopicName;
    }

    public String getSimpleExecutionRequestTopicName() {
        return simpleExecutionRequestTopicName;
    }
}
