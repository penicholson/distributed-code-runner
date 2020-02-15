package pl.petergood.dcr.jail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class NsJailTest {

    private static boolean isWindows = true;

    @BeforeAll
    public static void determineOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void verifyNsJailIsCalled() throws IOException {
        // given
        TerminalInteractor terminalInteractor = mock(TerminalInteractor.class);
        FileInteractor fileInteractor = mock(FileInteractor.class);
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/usr/share/config.cfg")
                .setHostJailPath(new File(isWindows ? "C:\\usr\\jail" : "/usr/jail"))
                .build();
        Jail jail = new NsJail(jailConfig, terminalInteractor, fileInteractor);

        String stdoutFilePath = isWindows ? "C:\\usr\\jail\\stdout" : "/usr/jail/stdout";
        String stderrFilePath = isWindows ? "C:\\usr\\jail\\stderr" : "/usr/jail/stderr";
        String[] expectedCommand = new String[] { "nsjail", "--config /usr/share/config.cfg", "--", "echo", "hello",
                ">", stdoutFilePath, "2>", stderrFilePath };

        when(terminalInteractor.exec(expectedCommand)).thenReturn(new ExecutionResult(0, "", ""));
        when(fileInteractor.readFile(new File(stdoutFilePath))).thenReturn("output");
        when(fileInteractor.readFile(new File(stderrFilePath))).thenReturn("");

        // when
        ExecutionResult result = jail.executeInJail(new String[] { "echo", "hello" });

        // then
        verify(terminalInteractor, times(1)).exec(expectedCommand);

        Assertions.assertThat(result.getStdOut()).isEqualTo("output");
        Assertions.assertThat(result.getStdErr()).isEmpty();
    }

}
