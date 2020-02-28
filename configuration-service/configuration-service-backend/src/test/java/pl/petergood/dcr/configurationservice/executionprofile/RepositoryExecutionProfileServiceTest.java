package pl.petergood.dcr.configurationservice.executionprofile;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import pl.petergood.dcr.configurationservice.ConfigurationServiceApplication;

import javax.persistence.EntityNotFoundException;

@ContextConfiguration(classes = ConfigurationServiceApplication.class)
@DataJpaTest
@Import(RepositoryExecutionProfileService.class)
public class RepositoryExecutionProfileServiceTest {

    @Autowired
    private ExecutionProfileService executionProfileService;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void verifyExecutionProfileIsQueriedById() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setMemoryLimitBytes(100);
        executionProfile.setCpuTimeLimitSeconds(10);
        ExecutionProfile persistedExecutionProfile = testEntityManager.persistAndFlush(executionProfile);

        // when
        ExecutionProfile profile = executionProfileService.findById(persistedExecutionProfile.getId());

        // then
        Assertions.assertThat(profile.getMemoryLimitBytes()).isEqualTo(100);
        Assertions.assertThat(profile.getCpuTimeLimitSeconds()).isEqualTo(10);
    }

    @Test
    public void verifyExceptionThrownWhenExecutionProfileNotFound() {
        // given
        // when
        Throwable thrownException = Assertions.catchThrowable(() -> executionProfileService.findById(7));

        // then
        Assertions.assertThat(thrownException).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void verifyExecutionProfileIsSaved() {
        // given
        ExecutionProfile executionProfile = new ExecutionProfile();
        executionProfile.setCpuTimeLimitSeconds(100);
        executionProfile.setMemoryLimitBytes(10);

        // when
        ExecutionProfile expectedExecutionProfile = executionProfileService.save(executionProfile);

        // then
        ExecutionProfile persistedExecutionProfile = testEntityManager.find(ExecutionProfile.class, expectedExecutionProfile.getId());
        Assertions.assertThat(persistedExecutionProfile.getCpuTimeLimitSeconds()).isEqualTo(100);
        Assertions.assertThat(persistedExecutionProfile.getMemoryLimitBytes()).isEqualTo(10);
    }

}
