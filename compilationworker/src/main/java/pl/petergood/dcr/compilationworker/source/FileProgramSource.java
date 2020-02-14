package pl.petergood.dcr.compilationworker.source;

import java.io.File;

public class FileProgramSource implements ProgramSource {

    private File file;

    public FileProgramSource(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }
}
