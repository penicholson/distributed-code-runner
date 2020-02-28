package pl.petergood.dcr.jail;

import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;
import java.util.UUID;

public class JailFactory {

    public static Jail createJail(File rootPath, String configPath, TerminalInteractor terminalInteractor, JailDirectoryMode directoryMode) {
        return createJail(rootPath, configPath, terminalInteractor, directoryMode, 0, 0);
    }

    public static Jail createJail(File rootPath, String configPath, TerminalInteractor terminalInteractor, JailDirectoryMode directoryMode, int cpuTimeLimitSeconds, int memoryLimitBytes) {
        UUID jailId = UUID.randomUUID();
        NsJailConfig.Builder jailConfigBuilder = new NsJailConfig.Builder()
                .setConfig(configPath)
                .setJailDirectoryName("jail", directoryMode)
                .setHostJailPath(new File(rootPath + "/" + jailId.toString()));

        NsJailProcessLimitConfig.Builder processLimitBuilder = new NsJailProcessLimitConfig.Builder();

        if (cpuTimeLimitSeconds != 0) {
            processLimitBuilder.cpuTimeLimit(cpuTimeLimitSeconds);
        }

        if (memoryLimitBytes != 0) {
            processLimitBuilder.memoryLimit(memoryLimitBytes);
        }

        jailConfigBuilder.setProcessLimitConfig(processLimitBuilder.build());

        return new NsJail(jailConfigBuilder.build(), terminalInteractor);
    }

}
