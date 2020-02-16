package pl.petergood.dcr.compilationworker.source;

import pl.petergood.dcr.compilationworker.language.LanguageId;
import pl.petergood.dcr.jail.JailedFile;

public class FileProgramSource implements ProgramSource {

    private JailedFile file;
    private LanguageId languageId;

    public FileProgramSource(JailedFile file, LanguageId languageId) {
        this.file = file;
        this.languageId = languageId;
    }

    @Override
    public JailedFile getJailedFile() {
        return file;
    }

    @Override
    public LanguageId getLanguageId() {
        return languageId;
    }
}
