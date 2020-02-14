package pl.petergood.dcr.shell;

import java.io.File;
import java.util.Map;

public interface TerminalInteractor {
    ExecutionResult exec(String[] commandParts);
    ExecutionResult exec(String[] commandParts, Map<String, String> envVars);
    ExecutionResult exec(String[] commandParts, Map<String, String> envVars, File workingDir);
}
