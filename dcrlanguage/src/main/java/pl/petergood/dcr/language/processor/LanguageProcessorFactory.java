package pl.petergood.dcr.language.processor;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.UnknownLanguageException;

public class LanguageProcessorFactory {

    public static LanguageProcessor getLanguage(LanguageId languageId, Jail jail) {
        switch (languageId) {
            case CPP:
                return new CppLanguageProcessor(jail);
            default:
                throw new UnknownLanguageException();
        }
    }

}
