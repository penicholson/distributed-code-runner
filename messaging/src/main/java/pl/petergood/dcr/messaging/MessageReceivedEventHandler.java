package pl.petergood.dcr.messaging;

import java.util.List;

public interface MessageReceivedEventHandler<T> {
    void handleMessageBatch(List<T> message);
}
