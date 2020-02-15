package pl.petergood.dcr.compilationworker.language;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.compilationworker.source.FileProgramSource;
import pl.petergood.dcr.jail.Jail;

import java.io.File;

import static org.mockito.Mockito.*;

public class CppLanguageTest {

    private static boolean isWindows = true;

    @BeforeAll
    public static void determineOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void verifyGppCalled() {
        // given
        Jail jailMock = mock(Jail.class);
        CppLanguage cppLanguage = new CppLanguage(jailMock);

        // when
        ProcessingResult result = cppLanguage.process(new FileProgramSource(new File("/jail/program.cpp")));

        // then
        String expectedPath = "C:\\jail\\output";
        if (!isWindows) {
            expectedPath = "/jail/output";
        }
        Assertions.assertThat(result.getProcessedFile().getAbsolutePath()).isEqualTo(expectedPath);

        if (isWindows) {
            verify(jailMock, times(1)).executeInJail(new String[] { "g++", "C:\\jail\\program.cpp", "-o", "output" });
        } else {
            verify(jailMock, times(1)).executeInJail(new String[] { "g++", "/jail/program.cpp", "-o", "output" });
        }
    }

}
