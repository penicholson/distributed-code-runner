package pl.petergood.dcr.shell;

public class ExecutionResult {
    private int exitCode;
    private String stdOut;
    private String stdErr;

    public ExecutionResult(int exitCode, String stdOut, String stdErr) {
        this.exitCode = exitCode;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getStdErr() {
        return stdErr;
    }

    public int getExitCode() {
        return exitCode;
    }
}
