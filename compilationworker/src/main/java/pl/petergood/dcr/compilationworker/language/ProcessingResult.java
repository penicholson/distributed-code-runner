package pl.petergood.dcr.compilationworker.language;

import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.shell.ExecutionResult;

public class ProcessingResult {
    private JailedFile processedFile;
    private ExecutionResult executionResult;

    public ProcessingResult(JailedFile processedFile, ExecutionResult executionResult) {
        this.processedFile = processedFile;
        this.executionResult = executionResult;
    }

    public JailedFile getProcessedFile() {
        return processedFile;
    }

    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    public boolean success() {
        return executionResult.getStdErr().isEmpty();
    }
}
