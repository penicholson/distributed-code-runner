package pl.petergood.dcr.language.executor;

import pl.petergood.dcr.jail.ExecutableFile;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.shell.FileExecutionResult;

import java.io.File;

public class BinaryLanguageExecutor implements LanguageExecutor {

    private Jail jail;

    public BinaryLanguageExecutor(Jail jail) {
        this.jail = jail;
    }

    @Override
    public FileExecutionResult execute(ExecutableFile executable) {
        return jail.executeWithInputFileAndReturnOutputFiles(new String[] { executable.getAbsolutePath() }, null);
    }

    public FileExecutionResult execute(ExecutableFile executable, File stdinFile) {
        return jail.executeWithInputFileAndReturnOutputFiles(new String[] { executable.getAbsolutePath() }, stdinFile);
    }
}
