package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.jail.Jail;

public class LanguageFactory {

    public static Language getLanguage(LanguageId languageId, Jail jail) {
        switch (languageId) {
            case CPP:
                return new CppLanguage(jail);
            default:
                throw new UnknownLanguageException();
        }
    }

}
