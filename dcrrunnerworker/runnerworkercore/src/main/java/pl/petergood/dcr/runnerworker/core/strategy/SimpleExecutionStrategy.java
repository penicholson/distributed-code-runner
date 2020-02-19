package pl.petergood.dcr.runnerworker.core.strategy;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.messaging.schema.SimpleExecutionRequest;

public class SimpleExecutionStrategy implements ExecutionStrategy<SimpleExecutionRequest> {

    private Jail jail;

    public SimpleExecutionStrategy(Jail jail) {
        this.jail = jail;
    }

    public void execute(SimpleExecutionRequest executionRequest) {

    }
}
