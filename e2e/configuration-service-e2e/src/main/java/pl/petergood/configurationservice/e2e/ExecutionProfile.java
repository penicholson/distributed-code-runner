package pl.petergood.configurationservice.e2e;

public class ExecutionProfile {
    private int id;
    private int cpuTimeLimitSeconds;
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
