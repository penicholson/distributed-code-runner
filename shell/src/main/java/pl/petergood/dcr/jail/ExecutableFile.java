package pl.petergood.dcr.jail;

import java.io.File;

public class ExecutableFile extends JailedFile {
    private ExecutableFile(String pathname, Jail jail) {
        super(pathname, jail);
    }

    private ExecutableFile(File parentPath, String path, Jail jail) {
        super(parentPath, path, jail);
    }

    static ExecutableFile fromJailedFile(JailedFile jailedFile) {
        return new ExecutableFile(jailedFile.getAbsolutePath(), jailedFile.getJail());
    }
}
