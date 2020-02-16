package pl.petergood.dcr.jail;

import java.io.File;

public class JailedFile extends File {

    private Jail jail;

    public JailedFile(String pathname, Jail jail) {
        super(pathname);
        this.jail = jail;
    }

    public JailedFile(File parentPath, String path, Jail jail) {
        super(parentPath, path);
        this.jail = jail;
    }

    public Jail getJail() {
        return jail;
    }
}
