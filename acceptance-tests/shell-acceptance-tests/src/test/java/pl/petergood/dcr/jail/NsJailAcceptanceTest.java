package pl.petergood.dcr.jail;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.FileExecutionResult;
import pl.petergood.dcr.shell.ShellTerminalInteractor;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.nio.charset.Charset;

public class NsJailAcceptanceTest {

    private static NsJailConfig jailConfig = new NsJailConfig.Builder()
            .setConfig("/test-nsjail.cfg")
            .setHostJailPath(new File("/nsjail"))
            .setJailDirectoryName("jail", JailDirectoryMode.READ_WRITE)
            .build();

    @Test
    public void verifyExecutionInNsJail() {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/bin/echo", "hello world" });

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
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/usr/bin/g++", "nonexistentfile" });

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
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/bin/bash", "-c", "\"exit 31\"" });

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
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/bin/pwd" });

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
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/usr/bin/touch", "test_file" });

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
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/usr/bin/touch", "/test_file" });

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
                .setJailDirectoryName("jail", JailDirectoryMode.READ_ONLY)
                .build();
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        ExecutionResult result = jail.executeAndReturnOutputContent(new String[] { "/usr/bin/touch", "test_file" });

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
        Throwable thrownException = Assertions.catchThrowable(() -> jail.executeAndReturnOutputContent(new String[] { "/bin/this_file_does_not_exist" }));

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

    @Test
    public void verifyJailIsDestroyed() throws Exception {
        // given
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);
        JailedFile jailedFile = jail.touchFile("test.txt", "hello there!");

        // when
        jail.destroy();

        // then
        Assertions.assertThat(jailConfig.getHostJailPath().exists()).isFalse();
        Assertions.assertThat(jailedFile.exists()).isFalse();
    }

    @Test
    public void verifyStdInContentIsUsed() throws Exception {
        // given
        String stdin = "hello there!";
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        FileExecutionResult result = jail.executeWithInputContentAndReturnOutputFiles(new String[] { "/bin/cat" }, stdin);

        // then
        String content = Files.asCharSource(result.getStdOutFile(), Charset.defaultCharset()).read();
        Assertions.assertThat(content).isEqualTo("hello there!");
    }

    @Test
    public void verifyStdInFileIsUsed() throws Exception {
        // given
        String stdin = "hello again!";
        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);
        File inputFile = new File(jail.getHostJailPath(), "stdin");
        Files.asCharSink(inputFile, Charset.defaultCharset()).write(stdin);

        // when
        FileExecutionResult result = jail.executeWithInputFileAndReturnOutputFiles(new String[] { "/bin/cat" }, inputFile);

        // then
        String content = Files.asCharSource(result.getStdOutFile(), Charset.defaultCharset()).read();
        Assertions.assertThat(content).isEqualTo("hello again!");
    }

    @Test
    public void verifyFileIsCopiedIntoJail() throws Exception {
        // given
        String contents = "this is a test!";
        File file = new File("/discovery.txt");
        Files.asCharSink(file, Charset.defaultCharset()).write(contents);

        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        JailedFile jailedFile = jail.jailFile(file);

        // then
        Assertions.assertThat(jailedFile.exists()).isTrue();
        Assertions.assertThat(file.exists()).isTrue();
        Assertions.assertThat(Files.asCharSource(jailedFile, Charset.defaultCharset()).read()).isEqualTo("this is a test!");
    }

    @Test
    public void verifyCpuLimitIsHonoured() throws Exception {
        // given
        File spinner = new File("/dcr/acceptance-tests/testbinaries/spinner");

        NsJailProcessLimitConfig processLimitConfig = new NsJailProcessLimitConfig.Builder()
                .cpuTimeLimit(1)
                .build();

        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", JailDirectoryMode.READ_WRITE)
                .setProcessLimitConfig(processLimitConfig)
                .build();

        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);
        JailedFile jailedSpinner = jail.jailFile(spinner);
        ExecutableFile executable = jail.makeExecutable(jailedSpinner);

        // when
        Throwable thrownException = Assertions.catchThrowable(() -> jail.executeAndReturnOutputContent(new String[] { executable.getAbsolutePath() }));

        // then
        Assertions.assertThat(thrownException).isInstanceOf(NsJailTerminatedException.class);
    }

    @Test
    public void verifyMemoryLimitIsHonoured() throws Exception {
        // given
        File chrome = new File("/dcr/acceptance-tests/testbinaries/chrome");

        NsJailProcessLimitConfig processLimitConfig = new NsJailProcessLimitConfig.Builder()
                .memoryLimit(10000)
                .build();

        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", JailDirectoryMode.READ_WRITE)
                .setProcessLimitConfig(processLimitConfig)
                .build();

        TerminalInteractor terminalInteractor = new ShellTerminalInteractor();
        Jail jail = new NsJail(jailConfig, terminalInteractor);
        JailedFile jailedChrome = jail.jailFile(chrome);
        ExecutableFile executable = jail.makeExecutable(jailedChrome);

        // when
        Throwable thrownException = Assertions.catchThrowable(() -> jail.executeAndReturnOutputContent(new String[] { executable.getAbsolutePath() }));

        // then
        Assertions.assertThat(thrownException).isInstanceOf(NsJailTerminatedException.class);
    }

    @Test
    public void verifyWellBehavedProgramExecutes() throws Exception {
        // given
        File sumFile = new File("/dcr/acceptance-tests/testbinaries/sum");
        NsJailProcessLimitConfig processLimitConfig = new NsJailProcessLimitConfig.Builder()
                .cpuTimeLimit(5)
                .memoryLimit(1000000)
                .build();

        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", JailDirectoryMode.READ_WRITE)
                .setProcessLimitConfig(processLimitConfig)
                .build();

        Jail jail = new NsJail(jailConfig, new ShellTerminalInteractor());
        JailedFile jailedSum = jail.jailFile(sumFile);
        ExecutableFile executable = jail.makeExecutable(jailedSum);

        // when
        FileExecutionResult result = jail.executeWithInputContentAndReturnOutputFiles(new String[] { executable.getAbsolutePath() }, "3 2");

        // then
        String output = Files.asCharSource(result.getStdOutFile(), Charset.defaultCharset()).read();
        Assertions.assertThat(output).isEqualTo("5");
    }

}
