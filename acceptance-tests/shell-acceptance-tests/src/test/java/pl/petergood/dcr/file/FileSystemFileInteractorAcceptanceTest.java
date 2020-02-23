package pl.petergood.dcr.file;

import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;

public class FileSystemFileInteractorAcceptanceTest {

    @Test
    public void verifyFileContentsIsRead() throws Exception {
        // given
        String fileContents = "hello world!";
        File file = new File("/test.txt");
        Files.asCharSink(file, Charset.defaultCharset()).write(fileContents);
        FileInteractor fileInteractor = new FileSystemFileInteractor();

        // when
        String readFileContents = fileInteractor.readFileAsString(file);

        // then
        Assertions.assertThat(readFileContents).isEqualTo(fileContents);
    }

    @Test
    public void verifyFileIsWritten() throws Exception {
        // given
        File file = new File("/hello.txt");
        FileInteractor fileInteractor = new FileSystemFileInteractor();

        // when
        fileInteractor.writeFileAsString(file, "this is a test!");

        // then
        String contents = Files.asCharSource(file, Charset.defaultCharset()).read();
        Assertions.assertThat(contents).isEqualTo("this is a test!");
    }

}
