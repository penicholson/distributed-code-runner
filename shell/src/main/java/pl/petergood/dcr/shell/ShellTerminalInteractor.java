package pl.petergood.dcr.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class ShellTerminalInteractor implements TerminalInteractor {
    @Override
    public ExecutionResult exec(String[] commandParts) {
        return exec(commandParts, null, null);
    }

    @Override
    public ExecutionResult exec(String[] commandParts, Map<String, String> envVars) {
        return exec(commandParts, envVars, null);
    }

    @Override
    public ExecutionResult exec(String[] commandParts, Map<String, String> envVars, File workingDir) {
        Runtime runtime = Runtime.getRuntime();
        String[] envVarsKeyValue = null;
        StringBuilder stdOut = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();

        if (envVars != null) {
            envVarsKeyValue = new String[envVars.size()];
            envVars.entrySet().stream()
                    .map((entry) -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.toList())
                    .toArray(envVarsKeyValue);
        }

        try {
            Process spawnedProcess = runtime.exec(commandParts, envVarsKeyValue, workingDir);

            BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(spawnedProcess.getInputStream()));
            BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(spawnedProcess.getErrorStream()));
            String line = null;

            while ((line = stdOutReader.readLine()) != null) {
                stdOut.append(line);
                stdOut.append("\n");
            }

            while ((line = stdErrReader.readLine()) != null) {
                stdErr.append(line);
                stdErr.append("\n");
            }
        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        return new ExecutionResult(stdOut.toString(), stdErr.toString());
    }
}
