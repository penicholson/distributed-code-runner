package pl.petergood.dcr.compilationworker.job;

import com.google.common.io.Files;
import pl.petergood.dcr.compilationworker.language.LanguageProcessor;
import pl.petergood.dcr.compilationworker.language.LanguageFactory;
import pl.petergood.dcr.compilationworker.language.ProcessingResult;
import pl.petergood.dcr.compilationworker.source.ProgramSource;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;

import java.io.IOException;

public class CompilationJob implements Runnable {

    private ProgramSource programSource;
    private Jail jail;
    private FileInteractor fileInteractor;

    public CompilationJob(ProgramSource programSource,
                          Jail jail,
                          FileInteractor fileInteractor) {
        this.programSource = programSource;
        this.jail = jail;
        this.fileInteractor = fileInteractor;
    }

    @Override
    public void run() {
        LanguageProcessor languageProcessor = LanguageFactory.getLanguage(programSource.getLanguageId(), jail);
        ProcessingResult processingResult = languageProcessor.process(programSource);

        try {
            byte[] processedBytes = fileInteractor.readFileAsBytes(processingResult.getProcessedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
