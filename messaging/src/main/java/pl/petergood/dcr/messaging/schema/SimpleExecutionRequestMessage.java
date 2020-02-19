package pl.petergood.dcr.messaging.schema;

public class SimpleExecutionRequestMessage {

    private String languageId;
    private byte[] processedBytes;
    private String stdin;

    public SimpleExecutionRequestMessage() {
    }

    public SimpleExecutionRequestMessage(String languageId, byte[] processedBytes, String stdin) {
        this.languageId = languageId;
        this.processedBytes = processedBytes;
        this.stdin = stdin;
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
}
