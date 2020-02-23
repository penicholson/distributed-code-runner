package pl.petergood.dcr.language.executor;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.UnknownLanguageException;

public class LanguageExecutorFactory {

    public static LanguageExecutor getLanguageExecutor(LanguageId languageId, Jail jail) {
        switch (languageId) {
            case CPP:
                return new BinaryLanguageExecutor(jail);
            default:
                throw new UnknownLanguageException();
        }
    }

}
