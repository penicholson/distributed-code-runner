package pl.petergood.dcr.messaging;

import java.util.List;
import java.util.function.Consumer;

public class MessageReceivedEventHandlerFactory {

    public static <T> MessageReceivedEventHandler<T> getMessageReceivedEventHandler(Consumer<List<T>> consumer) {
        return new MessageReceivedEventHandler<T>() {
            private List<T> messages;

            @Override
            public void setMessagesToProcess(List<T> messages) {
                this.messages = messages;
            }

            @Override
            public void run() {
                consumer.accept(messages);
            }
        };
    }

}
