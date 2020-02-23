package pl.petergood.dcr.jail;

import java.util.HashMap;
import java.util.Map;

public class NsJailProcessLimitConfig {

    private Map<String, String> config;

    private NsJailProcessLimitConfig() {
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public static class Builder {

        private Map<String, String> config = new HashMap<>();

        public Builder cpuTimeLimit(int timeInSeconds) {
            config.put("rlimit_cpu", Integer.toString(timeInSeconds));
            return this;
        }

        public Builder memoryLimit(int limitInBytes) {
            config.put("cgroup_mem_max", Integer.toString(limitInBytes));
            return this;
        }

        public NsJailProcessLimitConfig build() {
            NsJailProcessLimitConfig nsJailProcessLimitConfig = new NsJailProcessLimitConfig();
            nsJailProcessLimitConfig.config = config;
            return nsJailProcessLimitConfig;
        }

    }

}
