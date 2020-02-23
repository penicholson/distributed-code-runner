package pl.petergood.dcr.language.executor;

import pl.petergood.dcr.jail.ExecutableFile;
import pl.petergood.dcr.shell.FileExecutionResult;

import java.io.File;

public interface LanguageExecutor {
    FileExecutionResult execute(ExecutableFile executable);
    FileExecutionResult execute(ExecutableFile executable, File stdinFile);
}
