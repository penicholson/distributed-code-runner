package pl.petergood.dcr.configurationservice.executionprofile;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.configurationservice.ConfigurationServiceApplication;

import javax.validation.ConstraintViolationException;
import java.util.List;

@ContextConfiguration(classes = ConfigurationServiceApplication.class)
@DataJpaTest
public class ExecutionProfileRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ExecutionProfileRepository executionProfileRepository;

    @Test
    public void verifyFindById() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setCpuTimeLimitSeconds(2);
        executionProfile.setMemoryLimitBytes(100);
        ExecutionProfile persisted = testEntityManager.persistAndFlush(executionProfile);

        // when
        ExecutionProfile ep = executionProfileRepository.findById(persisted.getId()).get();

        // then
        Assertions.assertThat(ep.getCpuTimeLimitSeconds()).isEqualTo(2);
        Assertions.assertThat(ep.getMemoryLimitBytes()).isEqualTo(100);
    }

    @Test
    public void verifyPersistence() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setMemoryLimitBytes(10);
        executionProfile.setCpuTimeLimitSeconds(10);

        // when
        executionProfileRepository.save(executionProfile);

        // then
        Iterable<ExecutionProfile> persisted = executionProfileRepository.findAll();
        List<ExecutionProfile> executionProfiles = ImmutableList.copyOf(persisted.iterator());

        Assertions.assertThat(executionProfiles.size()).isEqualTo(1);
        Assertions.assertThat(executionProfiles.get(0).getCpuTimeLimitSeconds()).isEqualTo(10);
        Assertions.assertThat(executionProfiles.get(0).getMemoryLimitBytes()).isEqualTo(10);
    }

    @Test
    public void verifyConstraints() {
        // given
        ExecutionProfile incorrect1 = new ExecutionProfile();
        incorrect1.setCpuTimeLimitSeconds(-10);
        incorrect1.setMemoryLimitBytes(10);

        ExecutionProfile incorrect2 = new ExecutionProfile();
        incorrect1.setCpuTimeLimitSeconds(10);
        incorrect1.setMemoryLimitBytes(-10);

        // when
        Throwable thrownException1 = Assertions.catchThrowable(() -> {
            executionProfileRepository.save(incorrect1);
            testEntityManager.flush();
        });

        Throwable thrownException2 = Assertions.catchThrowable(() -> {
            executionProfileRepository.save(incorrect2);
            testEntityManager.flush();
        });

        // then
        Assertions.assertThat(thrownException1).isInstanceOf(ConstraintViolationException.class);
        Assertions.assertThat(thrownException2).isInstanceOf(ConstraintViolationException.class);
    }
}
