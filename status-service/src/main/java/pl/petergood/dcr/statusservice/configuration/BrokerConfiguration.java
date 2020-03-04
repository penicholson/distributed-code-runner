package pl.petergood.dcr.statusservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {

    @Value("${dcr.statusservice.kafka.bootstrap.urls}")
    private String bootstrapUrls;

    @Value("${dcr.statusservice.consumer.group.name}")
    private String consumerGroupName;

    @Value("${dcr.statusservice.status.topic.name}")
    private String statusTopicName;

    public String getBootstrapUrls() {
        return bootstrapUrls;
    }

    public String getConsumerGroupName() {
        return consumerGroupName;
    }

    public String getStatusTopicName() {
        return statusTopicName;
    }
}
