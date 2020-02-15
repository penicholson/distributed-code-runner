package pl.petergood.dcr.compilationworker.source;

import pl.petergood.dcr.compilationworker.language.LanguageId;

import java.io.File;

public class FileProgramSource implements ProgramSource {

    private File file;
    private LanguageId languageId;

    public FileProgramSource(File file, LanguageId languageId) {
        this.file = file;
        this.languageId = languageId;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public LanguageId getLanguageId() {
        return languageId;
    }
}
