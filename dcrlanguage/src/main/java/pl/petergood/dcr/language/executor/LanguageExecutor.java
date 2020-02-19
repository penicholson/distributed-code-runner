package pl.petergood.dcr.language.executor;

import pl.petergood.dcr.jail.JailedFile;

public interface LanguageExecutor {
    void execute(JailedFile executable, JailedFile stdin, JailedFile stdout);
}
