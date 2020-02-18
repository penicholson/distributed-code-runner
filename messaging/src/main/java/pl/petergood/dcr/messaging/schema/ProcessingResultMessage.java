package pl.petergood.dcr.messaging.schema;

public class ProcessingResultMessage {

    private String languageId;
    private byte[] processedBytes;

    public ProcessingResultMessage() {
    }

    public ProcessingResultMessage(String languageId, byte[] processedBytes) {
        this.languageId = languageId;
        this.processedBytes = processedBytes;
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
}
