package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.util.UUID;

public class JailFactory {

    public static Jail createJail(File rootPath, String configPath, TerminalInteractor terminalInteractor, NsJailDirectoryMode directoryMode) {
        UUID jailId = UUID.randomUUID();
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig(configPath)
                .setJailDirectoryName("jail", directoryMode)
                .setHostJailPath(new File(rootPath + "/" + jailId.toString()))
                .build();

        return new NsJail(jailConfig, terminalInteractor);
    }

}
