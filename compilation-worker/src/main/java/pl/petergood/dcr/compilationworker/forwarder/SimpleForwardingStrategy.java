package pl.petergood.dcr.compilationworker.forwarder;

import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;

public class SimpleForwardingStrategy implements ForwardingStrategy {

    private MessageProducer<SimpleExecutionRequestMessage> simpleExecutionRequestProducer;
    private ProcessingRequestMessage processingRequestMessage;

    public SimpleForwardingStrategy(MessageProducer<SimpleExecutionRequestMessage> simpleExecutionRequestProducer,
                                    ProcessingRequestMessage processingRequestMessage) {
        this.simpleExecutionRequestProducer = simpleExecutionRequestProducer;
        this.processingRequestMessage = processingRequestMessage;
    }

    @Override
    public void forwardMessage(byte[] processedBytes) {
        SimpleExecutionRequestMessage simpleExecutionRequest = new SimpleExecutionRequestMessage(processingRequestMessage.getLanguageId(), processedBytes,
                processingRequestMessage.getStdin(), processingRequestMessage.getExecutionProfileId());
        simpleExecutionRequestProducer.publish(simpleExecutionRequest);
    }
}
