package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.jail.Jail;

public class LanguageFactory {

    public static LanguageProcessor getLanguage(LanguageId languageId, Jail jail) {
        switch (languageId) {
            case CPP:
                return new CppLanguageProcessor(jail);
            default:
                throw new UnknownLanguageException();
        }
    }

}
