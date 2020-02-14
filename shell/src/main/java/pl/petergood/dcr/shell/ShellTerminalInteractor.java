package pl.petergood.dcr.shell;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
        ProcessBuilder processBuilder = new ProcessBuilder(ArrayUtils.addAll(new String[] { "/bin/bash", "-c" }, StringUtils.join(commandParts, " ")));
        processBuilder.directory(workingDir);

        StringBuilder stdOut = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();
        int exitCode = 1;

        if (envVars != null) {
            processBuilder.environment().putAll(envVars);
        }

        try {
            Process spawnedProcess = processBuilder.start();
            exitCode = spawnedProcess.waitFor();

            BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(spawnedProcess.getInputStream()));
            BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(spawnedProcess.getErrorStream()));
            String line;

            while ((line = stdOutReader.readLine()) != null) {
                stdOut.append(line);
                stdOut.append("\n");
            }

            while ((line = stdErrReader.readLine()) != null) {
                stdErr.append(line);
                stdErr.append("\n");
            }
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        }

        return new ExecutionResult(exitCode, stdOut.toString(), stdErr.toString());
    }
}
