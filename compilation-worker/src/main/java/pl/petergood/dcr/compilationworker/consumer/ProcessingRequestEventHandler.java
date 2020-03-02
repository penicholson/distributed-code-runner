package pl.petergood.dcr.compilationworker.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.compilationworker.configuration.JailConfiguration;
import pl.petergood.dcr.compilationworker.forwarder.ForwardingStrategy;
import pl.petergood.dcr.compilationworker.forwarder.ForwardingStrategyFactory;
import pl.petergood.dcr.compilationworker.job.CompilationJob;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailFactory;
import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.jail.JailDirectoryMode;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.source.FileProgramSource;
import pl.petergood.dcr.language.source.ProgramSource;
import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.MessageReceivedEventHandler;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.util.List;

public class ProcessingRequestEventHandler implements MessageReceivedEventHandler<String, ProcessingRequestMessage> {

    private JailConfiguration jailConfiguration;
    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;

    private MessageProducer<String, StatusMessage> statusProducer;
    private ForwardingStrategyFactory forwardingStrategyFactory;

    private Logger LOG = LoggerFactory.getLogger(ProcessingRequestEventHandler.class);

    public ProcessingRequestEventHandler(JailConfiguration jailConfiguration,
                                         TerminalInteractor terminalInteractor,
                                         FileInteractor fileInteractor,
                                         MessageProducerConfiguration messageProducerConfiguration,
                                         ForwardingStrategyFactory forwardingStrategyFactory) {
        this.jailConfiguration = jailConfiguration;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
        this.statusProducer = messageProducerConfiguration.getStatusProducer();
        this.forwardingStrategyFactory = forwardingStrategyFactory;
    }

    @Override
    public void handleMessageBatch(List<Message<String, ProcessingRequestMessage>> messages) {
        messages.forEach((message) -> handleProcessingRequest(message.getKey(), message.getMessage()));
    }

    private void handleProcessingRequest(String correlationId, ProcessingRequestMessage processingRequest) {
        LOG.info("Handling {} request with corelId={}", processingRequest.getLanguageId(), correlationId);

        statusProducer.publish(correlationId, new StatusMessage(StatusEventType.PROCESSING_STARTED));

        Jail jail = JailFactory.createJail(jailConfiguration.getJailRootPath(), jailConfiguration.getJailConfigurationPath(),
                terminalInteractor, JailDirectoryMode.READ_WRITE);

        try {
            ForwardingStrategy forwardingStrategy = forwardingStrategyFactory.getForwardingStrategy(correlationId, processingRequest);
            LanguageId languageId = LanguageId.fromId(processingRequest.getLanguageId());

            JailedFile jailedSource = jail.touchFile("source." + languageId.getExtension(), processingRequest.getSource());
            ProgramSource programSource = new FileProgramSource(jailedSource, languageId);

            CompilationJob compilationJob = new CompilationJob(correlationId, programSource, jail, fileInteractor,
                    forwardingStrategy, statusProducer);
            // TODO: Run on separate thread?
            compilationJob.run();
        } catch (Exception ex) {
            LOG.error(ex.toString());
            ex.printStackTrace();
        } finally {
            // TODO: should we have an acceptance test for this?
            jail.destroy();
        }
    }
}
