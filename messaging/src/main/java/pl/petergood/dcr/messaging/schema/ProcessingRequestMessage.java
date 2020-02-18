package pl.petergood.dcr.messaging.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessingRequestMessage {
    @JsonProperty("languageId")
    private String languageId;

    @JsonProperty("source")
    private String source;

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
}
