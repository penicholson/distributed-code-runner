package pl.petergood.dcr.language.executor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.jail.*;
import pl.petergood.dcr.shell.ExecutionResult;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;

import static org.mockito.Mockito.*;

public class BinaryLanguageExecutorTest {

    private static boolean isWindows = true;

    @BeforeAll
    public static void determineOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    NsJailConfig jailConfig = new NsJailConfig.Builder()
            .setConfig("/usr/share/config.cfg")
            .setHostJailPath(new File("/nsjail"))
            .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
            .build();

    @Test
    public void verifyBinaryIsExecuted() throws Exception {
        // given
        TerminalInteractor terminalInteractorMock = mock(TerminalInteractor.class);
        when(terminalInteractorMock.exec(any())).thenReturn(new ExecutionResult(0, "", ""));

        Jail jail = new NsJail(jailConfig, terminalInteractorMock);
        Jail jailSpy = spy(jail);
        File inputFile = new File("/jail/stdin");
        JailedFile file = new JailedFile("/jail/exec", jail);
        ExecutableFile executable = jailSpy.makeExecutable(file);
        LanguageExecutor languageExecutor = new BinaryLanguageExecutor(jailSpy);

        // when
        languageExecutor.execute(executable, inputFile);

        // then
        verify(jailSpy, times(1)).executeWithInputFileAndReturnOutputFiles(new String[] { isWindows ? "C:\\jail\\exec" : "/jail/exec" }, inputFile);
    }

    @Test
    public void verifyBinaryIsExecutedWithNoInput() {
        // given
        TerminalInteractor terminalInteractorMock = mock(TerminalInteractor.class);
        when(terminalInteractorMock.exec(any())).thenReturn(new ExecutionResult(0, "", ""));

        Jail jail = new NsJail(jailConfig, terminalInteractorMock);
        Jail jailSpy = spy(jail);
        JailedFile file = new JailedFile("/jail/exec", jail);
        ExecutableFile executable = jailSpy.makeExecutable(file);
        LanguageExecutor languageExecutor = new BinaryLanguageExecutor(jailSpy);

        // when
        languageExecutor.execute(executable);

        // then
        verify(jailSpy, times(1)).executeWithInputFileAndReturnOutputFiles(new String[] { isWindows ? "C:\\jail\\exec" : "/jail/exec" }, null);
    }

}
