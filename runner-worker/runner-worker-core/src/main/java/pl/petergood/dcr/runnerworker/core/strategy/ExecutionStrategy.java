package pl.petergood.dcr.runnerworker.core.strategy;

import java.io.IOException;

public interface ExecutionStrategy<T, R> {
    R execute(T executionRequest) throws IOException;
}
