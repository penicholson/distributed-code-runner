package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.shell.ExecutionResult;

import java.io.File;

public class ProcessingResult {
    private File processedFile;
    private ExecutionResult executionResult;

    public ProcessingResult(File processedFile, ExecutionResult executionResult) {
        this.processedFile = processedFile;
        this.executionResult = executionResult;
    }

    public File getProcessedFile() {
        return processedFile;
    }

    public boolean success() {
        return executionResult.getStdErr().isEmpty();
    }
}
