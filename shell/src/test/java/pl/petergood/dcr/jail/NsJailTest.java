package pl.petergood.dcr.jail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class NsJailTest {

    private static boolean isWindows = true;

    TerminalInteractor terminalInteractor;
    FileInteractor fileInteractor;
    Jail jail;

    String hostJailPath = isWindows ? "C:\\usr\\nsjail" : "/usr/nsjail";
    NsJailConfig jailConfig = new NsJailConfig.Builder()
            .setConfig("/usr/share/config.cfg")
            .setHostJailPath(new File(hostJailPath))
            .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
            .build();

    String absoluteJailPath = isWindows ? "C:\\usr\\nsjail\\jail" : "/usr/nsjail/jail";
    String stdoutFilePath = isWindows ? "C:\\usr\\nsjail\\stdout" : "/usr/nsjail/stdout";
    String stderrFilePath = isWindows ? "C:\\usr\\nsjail\\stderr" : "/usr/nsjail/stderr";
    String jailLogFilePath = isWindows ? "C:\\usr\\nsjail\\jail.log" : "/usr/nsjail/jail.log";

    String[] expectedCommand = new String[] { "nsjail", "--config /usr/share/config.cfg --cwd " + absoluteJailPath + " --bindmount " + absoluteJailPath,
            "--log " + jailLogFilePath, "--", "echo", "hello",
            ">", stdoutFilePath, "2>", stderrFilePath };

    @BeforeAll
    public static void determineOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    @BeforeEach
    public void setupStubs() {
        terminalInteractor = mock(TerminalInteractor.class);
        fileInteractor = mock(FileInteractor.class);
        jail = new NsJail(jailConfig, terminalInteractor, fileInteractor);
    }

    @Test
    public void verifyNsJailIsCalled() throws IOException {
        // given
        when(terminalInteractor.exec(expectedCommand)).thenReturn(new ExecutionResult(0, "", ""));
        when(fileInteractor.readFile(new File(stdoutFilePath))).thenReturn("output");
        when(fileInteractor.readFile(new File(stderrFilePath))).thenReturn("");
        when(fileInteractor.readFile(new File(jailLogFilePath))).thenReturn("");

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "echo", "hello" });

        // then
        verify(terminalInteractor, times(1)).exec(expectedCommand);

        Assertions.assertThat(result.getStdOut()).isEqualTo("output");
        Assertions.assertThat(result.getStdErr()).isEmpty();
    }

    @Test
    public void verifyExceptionIsThrownWhenLogsContainError() throws Exception {
        // given
        String rawJailLogs = "[I][2020-02-16T16:26:24+0000] Uid map: inside_uid:0 outside_uid:0 count:1 newuidmap:false\n" +
                "[W][2020-02-16T16:26:24+0000][262] void cmdline::logParams(nsjconf_t*)():254 Process will be UID/EUID=0 in the global user namespace, and will have user root-level access to files\n" +
                "[I][2020-02-16T16:26:24+0000] Gid map: inside_gid:0 outside_gid:0 count:1 newgidmap:false\n" +
                "[W][2020-02-16T16:26:24+0000][262] void cmdline::logParams(nsjconf_t*)():264 Process will be GID/EGID=0 in the global user namespace, and will have group root-level access to files\n" +
                "[I][2020-02-16T16:26:24+0000] Executing '/usr/bin/touch' for '[STANDALONE MODE]'\n" +
                "[E][2020-02-16T16:26:24+0000] some error :( \n" +
                "[I][2020-02-16T16:26:24+0000] pid=263 ([STANDALONE MODE]) exited with status: 0, (PIDs left: 0)";

        when(terminalInteractor.exec(expectedCommand)).thenReturn(new ExecutionResult(0, "", ""));
        when(fileInteractor.readFile(new File(stdoutFilePath))).thenReturn("");
        when(fileInteractor.readFile(new File(stderrFilePath))).thenReturn("");
        when(fileInteractor.readFile(new File(jailLogFilePath))).thenReturn(rawJailLogs);

        // when
        Throwable thrownException = Assertions.catchThrowable(() -> jail.executeInJail(new String[] { "echo", "hello" }));

        // then
        Assertions.assertThat(thrownException)
                .isInstanceOf(NsJailException.class)
                .hasMessage(rawJailLogs);
    }

}
