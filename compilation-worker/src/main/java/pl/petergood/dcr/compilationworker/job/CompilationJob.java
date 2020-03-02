package pl.petergood.dcr.compilationworker.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.compilationworker.forwarder.ForwardingStrategy;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.language.processor.LanguageProcessorFactory;
import pl.petergood.dcr.language.processor.LanguageProcessor;
import pl.petergood.dcr.language.processor.ProcessingResult;
import pl.petergood.dcr.language.source.ProgramSource;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingRequestMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;
import pl.petergood.dcr.messaging.status.StatusEventType;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.io.IOException;

public class CompilationJob implements Runnable {

    private String correlationId;
    private ProgramSource programSource;
    private Jail jail;
    private FileInteractor fileInteractor;
    private ForwardingStrategy forwardingStrategy;
    private MessageProducer<String, StatusMessage> statusProducer;

    private Logger LOG = LoggerFactory.getLogger(CompilationJob.class);

    public CompilationJob(String correlationId,
                          ProgramSource programSource,
                          Jail jail,
                          FileInteractor fileInteractor,
                          ForwardingStrategy forwardingStrategy,
                          MessageProducer<String, StatusMessage> statusProducer) {
        this.correlationId = correlationId;
        this.programSource = programSource;
        this.jail = jail;
        this.fileInteractor = fileInteractor;
        this.forwardingStrategy = forwardingStrategy;
        this.statusProducer = statusProducer;
    }

    @Override
    public void run() {
        LOG.info("Processing language {} with corelId={}", programSource.getLanguageId(), correlationId);

        LanguageProcessor languageProcessor = LanguageProcessorFactory.getLanguage(programSource.getLanguageId(), jail);
        ProcessingResult processingResult = languageProcessor.process(programSource);

        if (!processingResult.getExecutionResult().getStdErr().isEmpty()) {
            LOG.info("Processing {} resulted in failure with corelId={}", programSource.getLanguageId(), correlationId);
            StatusMessage statusMessage = new StatusMessage(StatusEventType.PROCESSING_FAILURE, processingResult.getExecutionResult().getStdErr());
            statusProducer.publish(correlationId, statusMessage);
            return;
        }

        try {
            byte[] processedBytes = fileInteractor.readFileAsBytes(processingResult.getProcessedFile());
            LOG.info("Processing {} resulted in success with corelId={}", programSource.getLanguageId(), correlationId);

            statusProducer.publish(correlationId, new StatusMessage(StatusEventType.PROCESSING_SUCCESS));
            forwardingStrategy.forwardMessage(processedBytes);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
