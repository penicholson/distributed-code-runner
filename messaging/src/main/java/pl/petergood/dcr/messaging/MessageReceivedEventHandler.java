package pl.petergood.dcr.messaging;

import java.util.List;

public interface MessageReceivedEventHandler<K, V> {
    void handleMessageBatch(List<Message<K, V>> message);
}
