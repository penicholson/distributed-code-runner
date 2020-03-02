package pl.petergood.dcr.compilationworker.forwarder;

import org.springframework.stereotype.Component;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;

@Component
public class ForwardingStrategyFactory {

    private MessageProducer<String, ProcessingResultMessage> processingResultProducer;
    private MessageProducer<String, SimpleExecutionRequestMessage> simpleExecutionRequestProducer;

    public ForwardingStrategyFactory(MessageProducerConfiguration producerConfiguration) {
        this.processingResultProducer = producerConfiguration.getProcessingResultProducer();
        this.simpleExecutionRequestProducer = producerConfiguration.getSimpleExecutionRequestProducer();
    }

    public ForwardingStrategy getForwardingStrategy(String correlationId, ProcessingRequestMessage processingRequestMessage) {
        if (processingRequestMessage.getForwardingType() == null) {
            return new ReturnForwardingStrategy(correlationId, processingResultProducer, processingRequestMessage);
        }

        switch (processingRequestMessage.getForwardingType()) {
            case NONE:
                return new ReturnForwardingStrategy(correlationId, processingResultProducer, processingRequestMessage);
            case SIMPLE:
                return new SimpleForwardingStrategy(correlationId, simpleExecutionRequestProducer, processingRequestMessage);
            default:
                throw new IllegalArgumentException();
        }
    }

}
