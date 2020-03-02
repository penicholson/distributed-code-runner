package pl.petergood.dcr.messaging;

public interface MessageProducer<K, V> {
    void publish(K key, V message);
    void close();
}
