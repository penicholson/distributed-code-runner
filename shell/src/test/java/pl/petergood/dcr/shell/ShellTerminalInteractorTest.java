package pl.petergood.dcr.shell;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShellTerminalInteractorTest {

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
        ExecutionResult result = interactor.exec(new String[] { "echozzz", "hello" });

        // then
        // TODO: find error msg
        Assertions.assertThat(result.getStdErr()).isEqualTo("The command echozzz was not found\n");
        Assertions.assertThat(result.getStdOut()).isEmpty();
    }

}
