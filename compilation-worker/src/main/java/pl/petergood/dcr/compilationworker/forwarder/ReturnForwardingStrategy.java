package pl.petergood.dcr.compilationworker.forwarder;

import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;

public class ReturnForwardingStrategy implements ForwardingStrategy {

    private MessageProducer<ProcessingResultMessage> processingResultProducer;
    private ProcessingRequestMessage processingRequest;

    public ReturnForwardingStrategy(MessageProducer<ProcessingResultMessage> processingResultProducer,
                                    ProcessingRequestMessage processingRequest) {
        this.processingResultProducer = processingResultProducer;
        this.processingRequest = processingRequest;
    }

    @Override
    public void forwardMessage(byte[] processedBytes) {
        ProcessingResultMessage message = new ProcessingResultMessage(processingRequest.getLanguageId(), processedBytes);
        processingResultProducer.publish(message);
    }
}
