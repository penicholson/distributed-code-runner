package pl.petergood.dcr.jail;

import com.google.common.io.Files;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.file.FileSystemFileInteractor;
import pl.petergood.dcr.shell.ExecutionException;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.FileExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class NsJail implements Jail {

    private Logger LOG = LoggerFactory.getLogger(NsJail.class);

    private NsJailConfig jailConfig;
    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;

    private NsJailErrorDetector logErrorDetector = new NsJailErrorDetector();

    public NsJail(NsJailConfig jailConfig, TerminalInteractor terminalInteractor) {
        this(jailConfig, terminalInteractor, new FileSystemFileInteractor());
    }

    public NsJail(NsJailConfig jailConfig,
                  TerminalInteractor terminalInteractor,
                  FileInteractor fileInteractor) {
        this.jailConfig = jailConfig;
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;

        setupJailDirectory();
    }

    private void setupJailDirectory() {
        // TODO: think about this...
        if (jailConfig.getHostJailPath().getAbsolutePath().equals("/")) {
            throw new IllegalStateException();
        }

        destroy();
        terminalInteractor.exec(new String[] { "mkdir", "-p", jailConfig.getHostJailPath().getAbsolutePath() });
        terminalInteractor.exec(new String[] { "mkdir", "-p", jailConfig.getAbsoluteJailPath().getAbsolutePath() });
    }

    @Override
    public FileExecutionResult executeWithInputFileAndReturnOutputFiles(String[] commandParts, File stdinFile) {
        File stdoutFile = new File(jailConfig.getHostJailPath(), "stdout");
        File stderrFile = new File(jailConfig.getHostJailPath(), "stderr");
        File jailLogFile = new File(jailConfig.getHostJailPath(), "jail.log");

        String commandFlags  = jailConfig.getCommandFlags("log");
        String[] nsJailCommand = ArrayUtils.addAll(
                ArrayUtils.addAll(new String[] { "nsjail", commandFlags,
                "--log " + jailLogFile.getAbsolutePath(), "--" }, commandParts),
                ">", stdoutFile.getAbsolutePath(),
                "2>", stderrFile.getAbsolutePath());

        String[] nsJailCommandWithInput;
        if (stdinFile != null) {
            nsJailCommandWithInput = ArrayUtils.addAll(nsJailCommand, "<", stdinFile.getAbsolutePath());
        } else {
            nsJailCommandWithInput = nsJailCommand;
        }

        ExecutionResult result = terminalInteractor.exec(nsJailCommandWithInput);

        validateJailLogs(jailLogFile);
        return new FileExecutionResult(result.getExitCode(), stdoutFile, stderrFile, jailLogFile);
    }

    @Override
    public FileExecutionResult executeWithInputContentAndReturnOutputFiles(String[] commandParts, String stdinContent) throws IOException {
        File stdinFile = new File(jailConfig.getHostJailPath(), "stdin");
        Files.asCharSink(stdinFile, Charset.defaultCharset()).write(stdinContent);
        return executeWithInputFileAndReturnOutputFiles(commandParts, stdinFile);
    }

    @Override
    public FileExecutionResult executeAndReturnOutputFiles(String[] commandParts) {
        return executeWithInputFileAndReturnOutputFiles(commandParts, null);
    }

    @Override
    public ExecutionResult executeAndReturnOutputContent(String[] commandParts) {
        FileExecutionResult result = executeAndReturnOutputFiles(commandParts);
        String stdout, stderr;

        try {
            stdout = fileInteractor.readFileAsString(result.getStdOutFile());
            stderr = fileInteractor.readFileAsString(result.getStdErrFile());
        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        return new ExecutionResult(result.getExitCode(), stdout, stderr);
    }

    private void validateJailLogs(File jailLogFile) {
        try {
            String jailLogs = fileInteractor.readFileAsString(jailLogFile);
            if (logErrorDetector.isErrorPresent(jailLogs)) {
                throw new NsJailException(jailLogs);
            }
        } catch (IOException e) {
            LOG.warn("Could not read log file {}", jailLogFile.getAbsolutePath());
        }
    }

    @Override
    public JailedFile touchFile(String fileName, String contents) throws IOException {
        JailedFile jailedFile = new JailedFile(jailConfig.getAbsoluteJailPath(), fileName, this);
        fileInteractor.writeFileAsString(jailedFile, contents);
        return jailedFile;
    }

    @Override
    public JailedFile jailFile(File file) throws IOException {
        JailedFile jailedFile = new JailedFile(jailConfig.getAbsoluteJailPath(), file.getName(), this);
        Files.copy(file, jailedFile);
        return jailedFile;
    }

    @Override
    public ExecutableFile makeExecutable(JailedFile file) {
        terminalInteractor.exec(new String[] { "/bin/chmod", "+x", file.getAbsolutePath() });
        return ExecutableFile.fromJailedFile(file);
    }

    @Override
    public File getAbsoluteJailPath() {
        return jailConfig.getAbsoluteJailPath();
    }

    @Override
    public File getHostJailPath() {
        return jailConfig.getHostJailPath();
    }

    @Override
    public void destroy() {
        terminalInteractor.exec(new String[] { "rm", "-r", "-f", jailConfig.getHostJailPath().getAbsolutePath() });
    }
}
