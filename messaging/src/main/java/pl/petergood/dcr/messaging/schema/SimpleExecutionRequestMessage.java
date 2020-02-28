package pl.petergood.dcr.messaging.schema;

public class SimpleExecutionRequestMessage {

    private String languageId;
    private byte[] processedBytes;
    private String stdin;
    private int executionProfileId;

    public SimpleExecutionRequestMessage() {
    }

    public SimpleExecutionRequestMessage(String languageId, byte[] processedBytes, String stdin, int executionProfileId) {
        this.languageId = languageId;
        this.processedBytes = processedBytes;
        this.stdin = stdin;
        this.executionProfileId = executionProfileId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public byte[] getProcessedBytes() {
        return processedBytes;
    }

    public void setProcessedBytes(byte[] processedBytes) {
        this.processedBytes = processedBytes;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public int getExecutionProfileId() {
        return executionProfileId;
    }

    public void setExecutionProfileId(int executionProfileId) {
        this.executionProfileId = executionProfileId;
    }
}
