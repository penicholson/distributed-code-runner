package pl.petergood.dcr.compilationworker.source;

import pl.petergood.dcr.compilationworker.language.LanguageId;

import java.io.File;

public interface ProgramSource {
    File getFile();
    LanguageId getLanguageId();
}
