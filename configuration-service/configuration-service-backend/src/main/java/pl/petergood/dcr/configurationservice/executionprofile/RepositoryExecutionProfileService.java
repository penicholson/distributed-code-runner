package pl.petergood.dcr.configurationservice.executionprofile;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class RepositoryExecutionProfileService implements ExecutionProfileService {

    private ExecutionProfileRepository executionProfileRepository;

    public RepositoryExecutionProfileService(ExecutionProfileRepository executionProfileRepository) {
        this.executionProfileRepository = executionProfileRepository;
    }

    @Override
    public ExecutionProfile findById(int id) {
        Optional<ExecutionProfile> executionProfile = executionProfileRepository.findById(id);
        if (!executionProfile.isPresent()) {
            throw new EntityNotFoundException();
        }

        return executionProfile.get();
    }

    @Override
    public ExecutionProfile save(ExecutionProfile executionProfile) {
        return executionProfileRepository.save(executionProfile);
    }
}
