package pl.petergood.dcr.runnerworker.core.strategy;

import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.ExecutableFile;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.executor.LanguageExecutor;
import pl.petergood.dcr.language.executor.LanguageExecutorFactory;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;
import pl.petergood.dcr.shell.FileExecutionResult;

import java.io.File;
import java.io.IOException;

public class SimpleExecutionStrategy implements ExecutionStrategy<SimpleExecutionRequestMessage, SimpleExecutionResultMessage> {

    private Jail jail;
    private FileInteractor fileInteractor;

    public SimpleExecutionStrategy(Jail jail,
                                   FileInteractor fileInteractor) {
        this.jail = jail;
        this.fileInteractor = fileInteractor;
    }

    public SimpleExecutionResultMessage execute(SimpleExecutionRequestMessage executionRequest) throws IOException {
        LanguageId languageId = LanguageId.fromId(executionRequest.getLanguageId());

        File stdinFile = new File(jail.getHostJailPath(), "stdin");
        fileInteractor.writeFileAsString(stdinFile, executionRequest.getStdin());

        // TODO: file extensions (for interpreted languages)?
        File executableFile = new File(jail.getHostJailPath(), "exec");
        fileInteractor.writeFileAsBytes(executableFile, executionRequest.getProcessedBytes());
        JailedFile jailedFile = jail.jailFile(executableFile);
        ExecutableFile executable = jail.makeExecutable(jailedFile);
        LanguageExecutor languageExecutor = LanguageExecutorFactory.getLanguageExecutor(languageId, jail);

        FileExecutionResult executionResult = languageExecutor.execute(executable, stdinFile);

        String stdout = fileInteractor.readFileAsString(executionResult.getStdOutFile());
        String stderr = fileInteractor.readFileAsString(executionResult.getStdErrFile());

        return new SimpleExecutionResultMessage(executionResult.getExitCode(), stdout, stderr);
    }
}
