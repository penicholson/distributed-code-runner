package pl.petergood.dcr.language.executor;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.petergood.dcr.acceptancetests.AcceptanceTestsJailFactory;
import pl.petergood.dcr.jail.ExecutableFile;
import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.JailedFile;
import pl.petergood.dcr.jail.NsJailDirectoryMode;
import pl.petergood.dcr.shell.FileExecutionResult;

import java.io.File;
import java.nio.charset.Charset;

public class BinaryLanguageExecutorAcceptanceTest {

    @Test
    public void verifyBinaryIsExecutedWithNoInput() throws Exception {
        // given
        Jail jail = AcceptanceTestsJailFactory.getJail(NsJailDirectoryMode.READ_ONLY);
        LanguageExecutor languageExecutor = new BinaryLanguageExecutor(jail);
        JailedFile file = jail.jailFile(new File("/dcr/dcracceptancetests/testbinaries/helloworld"));
        ExecutableFile executable = jail.makeExecutable(file);

        // when
        FileExecutionResult executionResult = languageExecutor.execute(executable);

        // then
        String output = Files.asCharSource(executionResult.getStdOutFile(), Charset.defaultCharset()).read();
        Assertions.assertThat(output).isEqualTo("Hello world!");
    }

    @Test
    public void verifyBinaryIsExecutedWithInput() throws Exception {
        // given
        Jail jail = AcceptanceTestsJailFactory.getJail(NsJailDirectoryMode.READ_ONLY);
        LanguageExecutor languageExecutor = new BinaryLanguageExecutor(jail);
        JailedFile file = jail.jailFile(new File("/dcr/dcracceptancetests/testbinaries/sum"));
        ExecutableFile executable = jail.makeExecutable(file);

        File inputFile = new File(jail.getHostJailPath(), "test-stdin");
        String input = "3 5";
        Files.asCharSink(inputFile, Charset.defaultCharset()).write(input);

        // when
        FileExecutionResult executionResult = languageExecutor.execute(executable, inputFile);

        // then
        String output = Files.asCharSource(executionResult.getStdOutFile(), Charset.defaultCharset()).read();
        Assertions.assertThat(output).isEqualTo("8");
    }

}
