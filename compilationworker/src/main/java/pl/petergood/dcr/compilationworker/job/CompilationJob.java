package pl.petergood.dcr.compilationworker.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.compilationworker.language.LanguageFactory;
import pl.petergood.dcr.compilationworker.language.LanguageProcessor;
import pl.petergood.dcr.compilationworker.language.ProcessingResult;
import pl.petergood.dcr.compilationworker.source.ProgramSource;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.schema.ProcessingFailureMessage;
import pl.petergood.dcr.messaging.schema.ProcessingResultMessage;

import java.io.IOException;

public class CompilationJob implements Runnable {

    private ProgramSource programSource;
    private Jail jail;
    private FileInteractor fileInteractor;
    private MessageProducer<ProcessingResultMessage> messageProducer;
    private MessageProducer<ProcessingFailureMessage> failureMessageProducer;

    private Logger LOG = LoggerFactory.getLogger(CompilationJob.class);

    public CompilationJob(ProgramSource programSource,
                          Jail jail,
                          FileInteractor fileInteractor,
                          MessageProducer<ProcessingResultMessage> messageProducer,
                          MessageProducer<ProcessingFailureMessage> failureMessageProducer) {
        this.programSource = programSource;
        this.jail = jail;
        this.fileInteractor = fileInteractor;
        this.messageProducer = messageProducer;
        this.failureMessageProducer = failureMessageProducer;
    }

    @Override
    public void run() {
        LOG.info("Processing language {}", programSource.getLanguageId());

        LanguageProcessor languageProcessor = LanguageFactory.getLanguage(programSource.getLanguageId(), jail);
        ProcessingResult processingResult = languageProcessor.process(programSource);

        if (!processingResult.getExecutionResult().getStdErr().isEmpty()) {
            LOG.info("Processing {} resulted in failure", programSource.getLanguageId());
            ProcessingFailureMessage failureMessage = new ProcessingFailureMessage(processingResult.getExecutionResult().getStdErr());
            failureMessageProducer.publish(failureMessage);
            return;
        }

        try {
            byte[] processedBytes = fileInteractor.readFileAsBytes(processingResult.getProcessedFile());

            LOG.info("Processing {} resulted in success", programSource.getLanguageId());
            ProcessingResultMessage message = new ProcessingResultMessage(programSource.getLanguageId().toString(), processedBytes);
            messageProducer.publish(message);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
