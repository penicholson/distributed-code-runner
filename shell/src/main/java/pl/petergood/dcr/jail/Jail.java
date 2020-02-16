package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.ExecutionResult;

import java.io.File;

public interface Jail {
    ExecutionResult executeInJail(String[] commandParts);
    File getJailPath();
}
