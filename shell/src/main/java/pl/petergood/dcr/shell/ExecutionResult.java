package pl.petergood.dcr.shell;

public class ExecutionResult {
    private String stdOut;
    private String stdErr;

    public ExecutionResult(String stdOut, String stdErr) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getStdErr() {
        return stdErr;
    }
}
