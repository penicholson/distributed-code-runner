package pl.petergood.dcr.runnerworker.simple.consumer;

import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailFactory;
import pl.petergood.dcr.jail.NsJailDirectoryMode;
import pl.petergood.dcr.jail.NsJailException;
import pl.petergood.dcr.messaging.MessageProducer;
import pl.petergood.dcr.messaging.MessageReceivedEventHandler;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequestMessage;
import pl.petergood.dcr.messaging.schema.SimpleExecutionResultMessage;
import pl.petergood.dcr.runnerworker.core.strategy.SimpleExecutionStrategy;
import pl.petergood.dcr.runnerworker.simple.configuration.JailConfiguration;
import pl.petergood.dcr.runnerworker.simple.producer.MessageProducerConfiguration;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.IOException;
import java.util.List;

public class SimpleExecutionRequestHandler implements MessageReceivedEventHandler<SimpleExecutionRequestMessage> {

    private TerminalInteractor terminalInteractor;
    private FileInteractor fileInteractor;
    private JailConfiguration jailConfiguration;
    private MessageProducer<SimpleExecutionResultMessage> executionResultMessageProducer;

    public SimpleExecutionRequestHandler(TerminalInteractor terminalInteractor,
                                         FileInteractor fileInteractor,
                                         JailConfiguration jailConfiguration,
                                         MessageProducerConfiguration messageProducerConfiguration) {
        this.terminalInteractor = terminalInteractor;
        this.fileInteractor = fileInteractor;
        this.jailConfiguration = jailConfiguration;
        this.executionResultMessageProducer = messageProducerConfiguration.getResultMessageProducer();
    }

    @Override
    public void setMessagesToProcess(List<SimpleExecutionRequestMessage> messages) {
        messages.forEach(this::handleMessage);
    }

    private void handleMessage(SimpleExecutionRequestMessage message) {
        Jail jail = JailFactory.createJail(jailConfiguration.getJailRootPath(), jailConfiguration.getJailConfigurationPath(),
                terminalInteractor, NsJailDirectoryMode.READ_ONLY);
        SimpleExecutionStrategy executionStrategy = new SimpleExecutionStrategy(jail, fileInteractor);

        try {
            SimpleExecutionResultMessage resultMessage = executionStrategy.execute(message);
            executionResultMessageProducer.publish(resultMessage);
        } catch (NsJailException | IOException ex) {
            // TODO: send error message
        }
    }
}
