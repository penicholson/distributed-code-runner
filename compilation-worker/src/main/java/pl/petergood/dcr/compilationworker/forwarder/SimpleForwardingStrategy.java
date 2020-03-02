package pl.petergood.dcr.compilationworker.forwarder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;

public class SimpleForwardingStrategy implements ForwardingStrategy {

    private String correlationId;
    private MessageProducer<String, SimpleExecutionRequestMessage> simpleExecutionRequestProducer;
    private ProcessingRequestMessage processingRequestMessage;

    private Logger LOG = LoggerFactory.getLogger(SimpleForwardingStrategy.class);

    public SimpleForwardingStrategy(String correlationId,
                                    MessageProducer<String, SimpleExecutionRequestMessage> simpleExecutionRequestProducer,
                                    ProcessingRequestMessage processingRequestMessage) {
        this.correlationId = correlationId;
        this.simpleExecutionRequestProducer = simpleExecutionRequestProducer;
        this.processingRequestMessage = processingRequestMessage;
    }

    @Override
    public void forwardMessage(byte[] processedBytes) {
        SimpleExecutionRequestMessage simpleExecutionRequest = new SimpleExecutionRequestMessage(processingRequestMessage.getLanguageId(), processedBytes,
                processingRequestMessage.getStdin(), processingRequestMessage.getExecutionProfileId());
        simpleExecutionRequestProducer.publish(correlationId, simpleExecutionRequest);
        LOG.info("SimpleForwardingStrategy completed for corelId={}", correlationId);
    }
}
