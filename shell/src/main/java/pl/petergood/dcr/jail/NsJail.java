package pl.petergood.dcr.jail;

import org.apache.commons.lang3.ArrayUtils;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.file.FileSystemFileInteractor;
import pl.petergood.dcr.shell.ExecutionException;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.io.IOException;

public class NsJail implements Jail {

    private NsJailConfig jailConfig;
    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;

    public NsJail(NsJailConfig jailConfig, TerminalInteractor terminalInteractor) {
        this(jailConfig, terminalInteractor, new FileSystemFileInteractor());
    }

    public NsJail(NsJailConfig jailConfig,
                  TerminalInteractor terminalInteractor,
                  FileInteractor fileInteractor) {
        this.jailConfig = jailConfig;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
    }

    @Override
    public ExecutionResult executeInJail(String[] commandParts) {
        String[] nsJailCommand = ArrayUtils.addAll(new String[] { "nsjail", jailConfig.getCommandFlags(), "--" }, commandParts);
        File stdoutFile = new File(jailConfig.getHostJailPath(), "stdout");
        File stderrFile = new File(jailConfig.getHostJailPath(), "stderr");

        ExecutionResult result = terminalInteractor.exec(ArrayUtils.addAll(
                nsJailCommand,
                ">", stdoutFile.getAbsolutePath(),
                "2>", stderrFile.getAbsolutePath()
        ));

        String stdout;
        String stderr;

        try {
            stdout = fileInteractor.readFile(stdoutFile);
            stderr = fileInteractor.readFile(stderrFile);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        return new ExecutionResult(result.getExitCode(), stdout, stderr);
    }
}
