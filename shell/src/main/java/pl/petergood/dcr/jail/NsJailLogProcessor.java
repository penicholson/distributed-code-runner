package pl.petergood.dcr.jail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NsJailLogProcessor {

    private static final Pattern logErrorPattern = Pattern.compile("^\\[E\\]", Pattern.MULTILINE);
    private static final Pattern logTimeoutPattern = Pattern.compile("^\\[I\\](.*)terminated with signal: SIGKILL \\(9\\)", Pattern.MULTILINE);

    public boolean isErrorPresent(String rawLogs) {
        Matcher logErrorMatcher = logErrorPattern.matcher(rawLogs);
        return logErrorMatcher.find();
    }

    public boolean didTerminate(String rawLogs) {
        Matcher timeoutMatcher = logTimeoutPattern.matcher(rawLogs);
        return timeoutMatcher.find();
    }

}
