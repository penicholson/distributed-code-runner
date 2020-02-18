package pl.petergood.dcr.file;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileSystemFileInteractor implements FileInteractor {
    @Override
    public String readFileAsString(File file) throws IOException {
        return Files.asCharSource(file, Charset.defaultCharset()).read();
    }

    @Override
    public byte[] readFileAsBytes(File file) throws IOException {
        return Files.asByteSource(file).read();
    }

    @Override
    public void writeFileAsString(File file, String contents) throws IOException {
        Files.asCharSink(file, Charset.defaultCharset()).write(contents);
    }
}
