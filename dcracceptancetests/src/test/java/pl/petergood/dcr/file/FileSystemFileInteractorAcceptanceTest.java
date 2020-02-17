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

}
