package pl.petergood.dcr.jail;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.ShellTerminalInteractor;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.nio.charset.Charset;

public class NsJailAcceptanceTest {

    private static NsJailConfig jailConfig = new NsJailConfig.Builder()
            .setConfig("/test-nsjail.cfg")
            .setHostJailPath(new File("/nsjail"))
            .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
            .build();

    @Test
    public void verifyExecutionInNsJail() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/bin/echo", "hello world" });

        // then
        Assertions.assertThat(result.getStdOut()).isEqualTo("hello world\n");
        Assertions.assertThat(result.getStdErr()).isEmpty();
        Assertions.assertThat(result.getExitCode()).isEqualTo(0);
    }

    @Test
    public void verifyStdErrIsCaptured() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/usr/bin/g++", "nonexistentfile" });

        // then
        Assertions.assertThat(result.getStdOut()).isEmpty();
        Assertions.assertThat(result.getStdErr()).isNotEmpty();
        Assertions.assertThat(result.getExitCode()).isEqualTo(1);
    }

    @Test
    public void verifyExitCodeOfJailedCommandIsCaptured() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/bin/bash", "-c", "\"exit 31\"" });

        // then
        Assertions.assertThat(result.getStdOut()).isEmpty();
        Assertions.assertThat(result.getStdErr()).isEmpty();
        Assertions.assertThat(result.getExitCode()).isEqualTo(31);
    }

    @Test
    public void verifyJailIsUsedAsWorkingDirectory() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/bin/pwd" });

        // then
        Assertions.assertThat(result.getStdOut()).isEqualTo("/nsjail/jail\n");
        Assertions.assertThat(result.getStdErr()).isEmpty();
        Assertions.assertThat(result.getExitCode()).isEqualTo(0);
    }

    @Test
    public void verifyJailDirectoryIsMountedInReadWriteMode() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/usr/bin/touch", "test_file" });

        // then
        Assertions.assertThat(result.getStdOut()).isEmpty();
        Assertions.assertThat(result.getStdErr()).isEmpty();
        Assertions.assertThat(result.getExitCode()).isEqualTo(0);
    }

    @Test
    public void verifyDirsOutsideOfJailAreMountedAsReadOnly() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/usr/bin/touch", "/test_file" });

        // then
        Assertions.assertThat(result.getStdOut()).isEmpty();
        Assertions.assertThat(result.getStdErr()).isEqualTo("/usr/bin/touch: cannot touch '/test_file': Read-only file system\n");
        Assertions.assertThat(result.getExitCode()).isNotEqualTo(0);
    }

    @Test
    public void verifyJailDirectoryIsMountedInReadOnlyMode() {
        // given
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_ONLY)
                .build();
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "/usr/bin/touch", "test_file" });

        // then
        Assertions.assertThat(result.getStdOut()).isEmpty();
        Assertions.assertThat(result.getStdErr()).isEqualTo("/usr/bin/touch: cannot touch 'test_file': Read-only file system\n");
        Assertions.assertThat(result.getExitCode()).isNotEqualTo(0);
    }

    @Test
    public void verifyExceptionIsThrownAfterJailError() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        Throwable thrownException = Assertions.catchThrowable(() -> jail.executeInJail(new String[] { "/bin/this_file_does_not_exist" }));

        // then
        Assertions.assertThat(thrownException).isInstanceOf(NsJailException.class);
    }

    @Test
    public void verifyFileIsCreatedInJail() throws Exception {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        JailedFile jailedFile = jail.touchFile("a_test_file.txt", "this is a \n test!");

        // then
        Assertions.assertThat(jailedFile.getAbsolutePath()).isEqualTo("/nsjail/jail/a_test_file.txt");
        Assertions.assertThat(jailedFile.exists()).isTrue();
        String contents = Files.asCharSource(jailedFile, Charset.defaultCharset()).read();
        Assertions.assertThat(contents).isEqualTo("this is a \n test!");
    }

}
