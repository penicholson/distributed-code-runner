package pl.petergood.dcr.compilationworker.source;

import pl.petergood.dcr.compilationworker.language.LanguageId;
import pl.petergood.dcr.jail.JailedFile;

public interface ProgramSource {
    JailedFile getJailedFile();
    LanguageId getLanguageId();
}
