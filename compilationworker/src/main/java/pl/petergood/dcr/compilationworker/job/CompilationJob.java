package pl.petergood.dcr.compilationworker.job;

import pl.petergood.dcr.compilationworker.language.Language;
import pl.petergood.dcr.compilationworker.language.LanguageFactory;
import pl.petergood.dcr.compilationworker.language.ProcessingResult;
import pl.petergood.dcr.compilationworker.source.ProgramSource;
import pl.petergood.dcr.jail.Jail;

public class CompilationJob implements Runnable {

    private ProgramSource programSource;
    private Language language;

    public CompilationJob(ProgramSource programSource, Jail jail) {
        this.programSource = programSource;
        this.language = LanguageFactory.getLanguage(programSource.getLanguageId(), jail);
    }

    @Override
    public void run() {
        ProcessingResult processingResult = language.process(programSource);
    }
}
