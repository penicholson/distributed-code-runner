package pl.petergood.dcr.configurationservice.executionprofile;

public interface ExecutionProfileService {
    ExecutionProfile findById(int id);
    ExecutionProfile save(ExecutionProfile executionProfile);
}
