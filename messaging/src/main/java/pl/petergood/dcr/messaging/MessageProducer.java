package pl.petergood.dcr.messaging;

public interface MessageProducer<T> {
    void publish(T message);
    void close();
}
