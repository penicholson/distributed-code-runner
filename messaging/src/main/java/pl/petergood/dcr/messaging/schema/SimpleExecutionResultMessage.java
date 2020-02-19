package pl.petergood.dcr.messaging.schema;

public class SimpleExecutionResultMessage {

    private int exitCode;
    private String stdout;
    private String stderr;

    public SimpleExecutionResultMessage() {
    }

    public SimpleExecutionResultMessage(int exitCode, String stdout, String stderr) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
}
