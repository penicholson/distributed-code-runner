package pl.petergood.dcr.messaging;

public interface MessageConsumer<T> {
    void setOnMessageReceived(MessageReceivedEventHandler<T> eventHandler);
    void close();
}
