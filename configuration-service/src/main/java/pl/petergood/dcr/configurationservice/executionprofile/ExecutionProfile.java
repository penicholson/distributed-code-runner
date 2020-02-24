package pl.petergood.dcr.configurationservice.executionprofile;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Data
public class ExecutionProfile {
    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Positive
    private int cpuTimeLimitSeconds;

    @NotNull
    @Positive
    private int memoryLimitBytes;
}
