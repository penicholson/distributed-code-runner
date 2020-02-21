package pl.petergood.dcr.messaging;

import java.util.List;

public interface MessageReceivedEventHandler<T> extends Runnable {
    void setMessagesToProcess(List<T> messages);
}
