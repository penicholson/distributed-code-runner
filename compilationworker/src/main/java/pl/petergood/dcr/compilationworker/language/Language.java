package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.compilationworker.source.ProgramSource;

public interface Language {
    ProcessingResult process(ProgramSource programSource);
}
