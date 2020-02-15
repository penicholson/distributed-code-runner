package pl.petergood.dcr.jail;

import org.apache.commons.lang3.ArrayUtils;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

public class NsJail implements Jail {

    private NsJailConfig jailConfig;
    private TerminalInteractor terminalInteractor;

    public NsJail(NsJailConfig jailConfig, TerminalInteractor terminalInteractor) {
        this.jailConfig = jailConfig;
        this.terminalInteractor = terminalInteractor;
    }

    @Override
    public ExecutionResult executeInJail(String[] commandParts) {
        String[] nsJailCommand = { "nsjail", jailConfig.getCommandFlags(), "--" };
        return terminalInteractor.exec(ArrayUtils.addAll(nsJailCommand, commandParts));
    }
}
