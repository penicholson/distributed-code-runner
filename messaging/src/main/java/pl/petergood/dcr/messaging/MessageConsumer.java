package pl.petergood.dcr.messaging;

public interface MessageConsumer<K, V> {
    void setOnMessageReceived(MessageReceivedEventHandler<K, V> eventHandler);
    void close();
}
