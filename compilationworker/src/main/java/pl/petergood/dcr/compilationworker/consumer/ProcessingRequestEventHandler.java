package pl.petergood.dcr.compilationworker.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.compilationworker.configuration.JailConfiguration;
import pl.petergood.dcr.compilationworker.job.CompilationJob;
import pl.petergood.dcr.compilationworker.producer.MessageProducerConfiguration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailFactory;
import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.jail.NsJailDirectoryMode;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.source.FileProgramSource;
import pl.petergood.dcr.language.source.ProgramSource;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.MessageReceivedEventHandler;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.util.List;

public class ProcessingRequestEventHandler implements MessageReceivedEventHandler<ProcessingRequestMessage> {

    private JailConfiguration jailConfiguration;
    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;

    private MessageProducer<ProcessingResultMessage> processingResultMessageProducer;
    private MessageProducer<ProcessingFailureMessage> processingFailureMessageProducer;

    private Logger LOG = LoggerFactory.getLogger(ProcessingRequestEventHandler.class);

    public ProcessingRequestEventHandler(JailConfiguration jailConfiguration,
                                         TerminalInteractor terminalInteractor,
                                         FileInteractor fileInteractor,
                                         MessageProducerConfiguration messageProducerConfiguration) {
        this.jailConfiguration = jailConfiguration;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
        this.processingResultMessageProducer = messageProducerConfiguration.getProcessingResultProducer();
        this.processingFailureMessageProducer = messageProducerConfiguration.getProcessingFailureProducer();
    }

    @Override
    public void handleMessageBatch(List<ProcessingRequestMessage> message) {
        message.forEach(this::handleProcessingRequest);
    }

    private void handleProcessingRequest(ProcessingRequestMessage processingRequest) {
        LOG.info("Handling {} request", processingRequest.getLanguageId());

        Jail jail = JailFactory.createJail(jailConfiguration.getJailRootPath(), jailConfiguration.getJailConfigurationPath(),
                terminalInteractor, NsJailDirectoryMode.READ_WRITE);

        try {
            LanguageId languageId = LanguageId.fromId(processingRequest.getLanguageId());

            JailedFile jailedSource = jail.touchFile("source." + languageId.getExtension(), processingRequest.getSource());
            ProgramSource programSource = new FileProgramSource(jailedSource, languageId);

            CompilationJob compilationJob = new CompilationJob(programSource, jail, fileInteractor,
                    processingResultMessageProducer, processingFailureMessageProducer);
            // TODO: Run on separate thread?
            compilationJob.run();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        } finally {
            // TODO: should we have an acceptance test for this?
            jail.destroy();
        }
    }
}
