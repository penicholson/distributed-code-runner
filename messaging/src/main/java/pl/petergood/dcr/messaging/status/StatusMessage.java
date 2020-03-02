package pl.petergood.dcr.messaging.status;

public class StatusMessage {
    private StatusEventType statusEventType;
    private String message;

    public StatusMessage() {
    }

    public StatusMessage(StatusEventType statusEventType, String message) {
        this.statusEventType = statusEventType;
        this.message = message;
    }

    public StatusMessage(StatusEventType statusEventType) {
        this(statusEventType, "");
    }

    public StatusEventType getStatusEventType() {
        return statusEventType;
    }

    public void setStatusEventType(StatusEventType statusEventType) {
        this.statusEventType = statusEventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
