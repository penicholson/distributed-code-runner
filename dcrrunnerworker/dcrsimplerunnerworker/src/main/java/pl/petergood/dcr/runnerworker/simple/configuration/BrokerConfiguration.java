package pl.petergood.dcr.runnerworker.simple.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {

    @Value("${dcr.simplerunnerworker.broker.bootstrap.urls}")
    private String brokerBootstrapUrls;

    @Value("${dcr.simplerunnerworker.broker.execution.request.topic.name}")
    private String executionRequestTopicName;

    @Value("${dcr.simplerunnerworker.broker.execution.result.topic.name}")
    private String executionResultTopicName;

    @Value("${dcr.simplerunnerworker.broker.group.name}")
    private String groupName;

    public String getBrokerBootstrapUrls() {
        return brokerBootstrapUrls;
    }

    public String getExecutionRequestTopicName() {
        return executionRequestTopicName;
    }

    public String getExecutionResultTopicName() {
        return executionResultTopicName;
    }

    public String getGroupName() {
        return groupName;
    }
}
