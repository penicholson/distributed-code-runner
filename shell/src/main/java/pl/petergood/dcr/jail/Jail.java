package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.FileExecutionResult;

import java.io.File;
import java.io.IOException;

public interface Jail {
    FileExecutionResult executeWithInputFileAndReturnOutputFiles(String[] commandParts, File stdinFile);
    FileExecutionResult executeWithInputContentAndReturnOutputFiles(String[] commandParts, String stdinContent) throws IOException;

    FileExecutionResult executeAndReturnOutputFiles(String[] commandParts);
    ExecutionResult executeAndReturnOutputContent(String[] commandParts);

    File getAbsoluteJailPath();
    File getHostJailPath();

    JailedFile touchFile(String fileName, String contents) throws IOException;

    void destroy();
}
