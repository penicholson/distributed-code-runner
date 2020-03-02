package pl.petergood.dcr.messaging.status;

public enum StatusEventType {
    PROCESSING_STARTED,
    PROCESSING_FAILURE,
    PROCESSING_SUCCESS,
    RUN_STARTED,
    RUN_FINISHED,
    RUN_VIOLATION,
    SYSTEM_FAILURE
}
