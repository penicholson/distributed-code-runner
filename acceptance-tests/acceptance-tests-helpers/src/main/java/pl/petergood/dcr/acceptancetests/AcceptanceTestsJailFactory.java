package pl.petergood.dcr.acceptancetests;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.NsJail;
import pl.petergood.dcr.jail.NsJailConfig;
import pl.petergood.dcr.jail.JailDirectoryMode;
import pl.petergood.dcr.shell.ShellTerminalInteractor;

import java.io.File;

public class AcceptanceTestsJailFactory {

    public static Jail getJail(JailDirectoryMode directoryMode) {
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", directoryMode)
                .build();

        return new NsJail(jailConfig, new ShellTerminalInteractor());
    }

}
