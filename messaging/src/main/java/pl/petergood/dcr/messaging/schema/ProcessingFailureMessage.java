package pl.petergood.dcr.messaging.schema;

public class ProcessingFailureMessage {
    private String error;

    public ProcessingFailureMessage() {
    }

    public ProcessingFailureMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
