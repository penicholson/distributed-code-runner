package pl.petergood.dcr.messaging;

public class Message<K, V> {
    private K key;
    private V message;

    public Message(K key, V message) {
        this.key = key;
        this.message = message;
    }

    public K getKey() {
        return key;
    }

    public V getMessage() {
        return message;
    }
}
