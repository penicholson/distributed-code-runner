package pl.petergood.dcr.shell;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ShellTerminalInteractorAcceptanceTest {

    @Test
    public void verifyCommandIsExecuted() {
        // given
        TerminalInteractor interactor = new ShellTerminalInteractor();

        // when
        ExecutionResult result = interactor.exec(new String[] { "echo", "hello" });

        // then
        Assertions.assertThat(result.getStdOut()).isEqualTo("hello\n");
        Assertions.assertThat(result.getStdErr()).isEmpty();
    }

    @Test
    public void verifyStdErrIsRecorded() {
        // given
        TerminalInteractor interactor = new ShellTerminalInteractor();

        // when
        ExecutionResult result = interactor.exec(new String[] { "echo", "error", ">&2" });

        // then
        Assertions.assertThat(result.getStdErr()).isEqualTo("error\n");
        Assertions.assertThat(result.getStdOut()).isEmpty();
    }

    @Test
    public void verifyEnvVarsAreApplied() {
        // given
        TerminalInteractor interactor = new ShellTerminalInteractor();

        // when
        ExecutionResult result = interactor.exec(new String[] { "echo", "$ENV_VAR" }, Collections.singletonMap("ENV_VAR", "testvalue"));

        // then
        Assertions.assertThat(result.getStdOut()).isEqualTo("testvalue\n");
        Assertions.assertThat(result.getStdErr()).isEmpty();
    }

}
