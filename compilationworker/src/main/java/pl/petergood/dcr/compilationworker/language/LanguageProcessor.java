package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.compilationworker.source.ProgramSource;

public interface LanguageProcessor {
    ProcessingResult process(ProgramSource programSource);
}
