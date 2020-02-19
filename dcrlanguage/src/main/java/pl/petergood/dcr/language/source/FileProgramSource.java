package pl.petergood.dcr.language.source;

import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.language.LanguageId;

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
