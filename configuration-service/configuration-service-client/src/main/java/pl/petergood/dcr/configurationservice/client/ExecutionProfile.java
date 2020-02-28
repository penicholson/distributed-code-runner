package pl.petergood.dcr.configurationservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecutionProfile {
    @JsonProperty("id")
    private int id;

    @JsonProperty("cpuTimeLimitSeconds")
    private int cpuTimeLimitSeconds;

    @JsonProperty("memoryLimitBytes")
    private int memoryLimitBytes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCpuTimeLimitSeconds() {
        return cpuTimeLimitSeconds;
    }

    public void setCpuTimeLimitSeconds(int cpuTimeLimitSeconds) {
        this.cpuTimeLimitSeconds = cpuTimeLimitSeconds;
    }

    public int getMemoryLimitBytes() {
        return memoryLimitBytes;
    }

    public void setMemoryLimitBytes(int memoryLimitBytes) {
        this.memoryLimitBytes = memoryLimitBytes;
    }
}
