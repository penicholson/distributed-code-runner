package pl.petergood.dcr.compilationworker.language;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.compilationworker.source.FileProgramSource;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailedFile;

import java.io.File;

import static org.mockito.Mockito.*;

public class CppLanguageProcessorTest {

    private static boolean isWindows = true;

    @BeforeAll
    public static void determineOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void verifyGppCalled() {
        // given
        Jail jailMock = mock(Jail.class);
        CppLanguageProcessor cppLanguage = new CppLanguageProcessor(jailMock);
        when(jailMock.getJailPath()).thenReturn(new File("/jail"));

        // when
        ProcessingResult result = cppLanguage.process(new FileProgramSource(new JailedFile("/jail/program.cpp", jailMock), LanguageId.CPP));

        // then
        String expectedPath = "C:\\jail\\output";
        if (!isWindows) {
            expectedPath = "/jail/output";
        }
        Assertions.assertThat(result.getProcessedFile().getAbsolutePath()).isEqualTo(expectedPath);

        if (isWindows) {
            verify(jailMock, times(1)).executeInJail(new String[] { "/usr/bin/g++", "C:\\jail\\program.cpp", "-o", "output" });
        } else {
            verify(jailMock, times(1)).executeInJail(new String[] { "/usr/bin/g++", "/jail/program.cpp", "-o", "output" });
        }
    }

}
