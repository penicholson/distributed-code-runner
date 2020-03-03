package pl.petergood.dcr.compilationworker.forwarder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;

public class ReturnForwardingStrategy implements ForwardingStrategy {

    private String correlationId;
    private MessageProducer<String, ProcessingResultMessage> processingResultProducer;
    private ProcessingRequestMessage processingRequest;

    private Logger LOG = LoggerFactory.getLogger(ReturnForwardingStrategy.class);

    public ReturnForwardingStrategy(String correlationId,
                                    MessageProducer<String, ProcessingResultMessage> processingResultProducer,
                                    ProcessingRequestMessage processingRequest) {
        this.correlationId = correlationId;
        this.processingResultProducer = processingResultProducer;
        this.processingRequest = processingRequest;
    }

    @Override
    public void forwardMessage(byte[] processedBytes) {
        ProcessingResultMessage message = new ProcessingResultMessage(processingRequest.getLanguageId(), processedBytes);
        processingResultProducer.publish(correlationId, message);
        LOG.info("ReturnForwardingStrategy completed for corelId={}", correlationId);
    }
}
