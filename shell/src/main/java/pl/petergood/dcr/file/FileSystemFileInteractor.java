package pl.petergood.dcr.file;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileSystemFileInteractor implements FileInteractor {
    @Override
    public String readFile(File file) throws IOException {
        return Files.asCharSource(file, Charset.defaultCharset()).read();
    }
}
