package pl.petergood.dcr.language.executor;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailedFile;

public class CppLanguageExecutor implements LanguageExecutor {

    private Jail jail;

    public CppLanguageExecutor(Jail jail) {
        this.jail = jail;
    }

    public void execute(JailedFile executable, JailedFile stdin, JailedFile stdout) {

    }
}
