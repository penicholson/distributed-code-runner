package pl.petergood.dcr.language.processor;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.acceptancetests.AcceptanceTestsJailFactory;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.jail.JailDirectoryMode;
import pl.petergood.dcr.language.LanguageId;
import pl.petergood.dcr.language.source.FileProgramSource;
import pl.petergood.dcr.language.source.ProgramSource;

import java.nio.charset.Charset;

public class CppLanguageProcessorAcceptanceTest {

    @Test
    public void verifyCppSourceIsCompiled() throws Exception {
        // given
        Jail jail = AcceptanceTestsJailFactory.getJail(JailDirectoryMode.READ_WRITE);
        JailedFile jailedFile = new JailedFile(jail.getAbsoluteJailPath(), "source.cpp", jail);
        CppLanguageProcessor cppLanguage = new CppLanguageProcessor(jail);
        String source = "#include <iostream>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "\treturn 0;\n" +
                "}";
        Files.asCharSink(jailedFile, Charset.defaultCharset()).write(source);
        ProgramSource programSource = new FileProgramSource(jailedFile, LanguageId.CPP);

        // when
        ProcessingResult processingResult = cppLanguage.process(programSource);

        // then
        Assertions.assertThat(processingResult.getExecutionResult().getStdErr()).isEmpty();
        Assertions.assertThat(processingResult.getExecutionResult().getStdOut()).isEmpty();
        Assertions.assertThat(processingResult.getExecutionResult().getExitCode()).isEqualTo(0);
        Assertions.assertThat(processingResult.getProcessedFile().exists()).isTrue();
    }

    @Test
    public void verifyInvalidCppSourceIsNotCompiled() throws Exception {
        // given
        Jail jail = AcceptanceTestsJailFactory.getJail(JailDirectoryMode.READ_WRITE);
        JailedFile jailedFile = new JailedFile(jail.getAbsoluteJailPath(), "source.cpp", jail);
        CppLanguageProcessor cppLanguage = new CppLanguageProcessor(jail);
        String source = " #include <iostream>\n" +
                "\n" +
                " using namespace std;\n" +
                "\n" +
                " int main() {\n" +
                "     asdf;\n" +
                "     return 0;\n" +
                " }\n";
        Files.asCharSink(jailedFile, Charset.defaultCharset()).write(source);
        ProgramSource programSource = new FileProgramSource(jailedFile, LanguageId.CPP);

        // when
        ProcessingResult processingResult = cppLanguage.process(programSource);

        // then
        Assertions.assertThat(processingResult.getExecutionResult().getStdErr()).contains("error: 'asdf' was not declared in this scope");
        Assertions.assertThat(processingResult.getExecutionResult().getStdOut()).isEmpty();
        Assertions.assertThat(processingResult.getExecutionResult().getExitCode()).isNotEqualTo(0);
        Assertions.assertThat(processingResult.getProcessedFile().exists()).isFalse();
    }

}
