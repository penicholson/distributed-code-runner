package pl.petergood.dcr.file;

import java.io.File;
import java.io.IOException;

public interface FileInteractor {
    String readFile(File file) throws IOException;
}
