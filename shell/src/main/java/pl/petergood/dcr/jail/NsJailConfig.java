package pl.petergood.dcr.jail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NsJailConfig {

    private Map<String, String> flags = new HashMap<>();
    private File hostJailPath;

    public String getCommandFlags() {
        StringBuilder commandFlags = new StringBuilder();
        flags.forEach((key, value) -> commandFlags.append(String.format("--%s %s ", key, value)));
        return commandFlags.toString().substring(0, commandFlags.length() - 1);
    }

    public File getHostJailPath() {
        return hostJailPath;
    }

    public static class Builder {
        private Map<String, String> flags = new HashMap<>();
        private File hostJailPath;

        public Builder setConfig(String path) {
            flags.put("config", path);
            return this;
        }

        public Builder setLogFile(String path) {
            flags.put("log", path);
            return this;
        }

        public Builder setHostJailPath(File path) {
            this.hostJailPath = path;
            return this;
        }

        public NsJailConfig build() {
            NsJailConfig jailConfig = new NsJailConfig();
            jailConfig.flags = this.flags;
            jailConfig.hostJailPath = this.hostJailPath;
            return jailConfig;
        }
    }

}
