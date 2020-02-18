package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.ExecutionResult;

import java.io.File;
import java.io.IOException;

public interface Jail {
    ExecutionResult executeInJail(String[] commandParts);
    File getJailPath();

    JailedFile touchFile(String fileName, String contents) throws IOException;
}
