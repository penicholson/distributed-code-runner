package pl.petergood.dcr.runnerworker.core.strategy;

public interface ExecutionStrategy<T> {
    void execute(T executionRequest);
}
