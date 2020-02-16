package pl.petergood.dcr.acceptancetests;

import pl.petergood.dcr.jail.Jail;
import pl.petergood.dcr.jail.NsJail;
import pl.petergood.dcr.jail.NsJailConfig;
import pl.petergood.dcr.jail.NsJailDirectoryMode;
import pl.petergood.dcr.shell.ShellTerminalInteractor;

import java.io.File;

public class AcceptanceTestsJailFactory {

    public static Jail getJail() {
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/test-nsjail.cfg")
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
                .build();

        return new NsJail(jailConfig, new ShellTerminalInteractor());
    }

}
