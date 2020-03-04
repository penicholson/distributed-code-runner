package pl.petergood.dcr.statusservice.consumer;

import pl.petergood.dcr.messaging.Message;
import pl.petergood.dcr.messaging.MessageReceivedEventHandler;
import pl.petergood.dcr.messaging.status.StatusMessage;

import java.util.List;

public class StatusReceivedEventHandler implements MessageReceivedEventHandler<String, StatusMessage> {
    @Override
    public void handleMessageBatch(List<Message<String, StatusMessage>> message) {

    }
}
