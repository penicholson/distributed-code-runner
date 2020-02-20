package pl.petergood.dcr.messaging.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessingRequestMessage {
    @JsonProperty("languageId")
    private String languageId;

    @JsonProperty("source")
    private String source;

    @JsonProperty(value = "forwardingType", defaultValue = "NONE")
    private ForwardingType forwardingType;

    @JsonProperty(value = "stdin", defaultValue = "")
    private String stdin;

    public ProcessingRequestMessage() {
    }

    public ProcessingRequestMessage(String languageId, String source) {
        this.languageId = languageId;
        this.source = source;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ForwardingType getForwardingType() {
        return forwardingType;
    }

    public void setForwardingType(ForwardingType forwardingType) {
        this.forwardingType = forwardingType;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }
}
