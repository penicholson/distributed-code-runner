package pl.petergood.dcr.jail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NsJailErrorDetector {

    private static final Pattern logErrorPattern = Pattern.compile("^\\[E\\]", Pattern.MULTILINE);

    public boolean isErrorPresent(String rawLogs) {
        Matcher logErrorMatcher = logErrorPattern.matcher(rawLogs);
        return logErrorMatcher.find();
    }

}
