package pl.petergood.dcr.compilationworker.job;

import pl.petergood.dcr.compilationworker.language.LanguageProcessor;
import pl.petergood.dcr.compilationworker.language.LanguageFactory;
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
        LanguageProcessor languageProcessor = LanguageFactory.getLanguage(programSource.getLanguageId(), jail);
        ProcessingResult processingResult = languageProcessor.process(programSource);

        if (!processingResult.getExecutionResult().getStdErr().isEmpty()) {
            ProcessingFailureMessage failureMessage = new ProcessingFailureMessage(processingResult.getExecutionResult().getStdErr());
            failureMessageProducer.publish(failureMessage);
            return;
        }

        try {
            byte[] processedBytes = fileInteractor.readFileAsBytes(processingResult.getProcessedFile());

            ProcessingResultMessage message = new ProcessingResultMessage(programSource.getLanguageId().toString(), processedBytes);
            messageProducer.publish(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
