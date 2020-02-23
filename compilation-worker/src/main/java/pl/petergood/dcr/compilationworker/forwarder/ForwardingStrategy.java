package pl.petergood.dcr.compilationworker.forwarder;

public interface ForwardingStrategy {
    void forwardMessage(byte[] processedBytes);
}
