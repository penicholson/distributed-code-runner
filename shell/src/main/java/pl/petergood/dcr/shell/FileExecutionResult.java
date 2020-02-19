package pl.petergood.dcr.shell;

import java.io.File;

public class FileExecutionResult {
    private int exitCode;
    private File stdOutFile;
    private File stdErrFile;
    private File jailLogFile;

    public FileExecutionResult(int exitCode, File stdOutFile, File stdErrFile, File jailLogFile) {
        this.exitCode = exitCode;
        this.stdOutFile = stdOutFile;
        this.stdErrFile = stdErrFile;
        this.jailLogFile = jailLogFile;
    }

    public int getExitCode() {
        return exitCode;
    }

    public File getStdOutFile() {
        return stdOutFile;
    }

    public File getStdErrFile() {
        return stdErrFile;
    }

    public File getJailLogFile() {
        return jailLogFile;
    }
}
