package pl.petergood.dcr.file;

import java.io.File;
import java.io.IOException;

public interface FileInteractor {
    String readFileAsString(File file) throws IOException;
    byte[] readFileAsBytes(File file) throws IOException;
}
