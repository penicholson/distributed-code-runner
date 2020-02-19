package pl.petergood.dcr.language.processor;

import pl.petergood.dcr.language.source.ProgramSource;

public interface LanguageProcessor {
    ProcessingResult process(ProgramSource programSource);
}
