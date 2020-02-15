package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.ExecutionResult;

public interface Jail {
    ExecutionResult executeInJail(String[] commandParts);
}
