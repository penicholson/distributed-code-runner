package pl.petergood.dcr.language.source;

import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.language.LanguageId;

public interface ProgramSource {
    JailedFile getJailedFile();
    LanguageId getLanguageId();
}
