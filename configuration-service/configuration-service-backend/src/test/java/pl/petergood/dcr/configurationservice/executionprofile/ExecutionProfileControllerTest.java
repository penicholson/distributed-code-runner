package pl.petergood.dcr.configurationservice.executionprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExecutionProfileController.class)
public class ExecutionProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExecutionProfileService executionProfileServiceMock;

    @Test
    public void verifyExecutionProfileIsReturned() throws Exception {
        // given
        ExecutionProfile testExecutionProfile = new ExecutionProfile();
        testExecutionProfile.setMemoryLimitBytes(100);
        testExecutionProfile.setCpuTimeLimitSeconds(10);
        when(executionProfileServiceMock.findById(1)).thenReturn(testExecutionProfile);

        // then
        mockMvc.perform(get("/executionprofile/1"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cpuTimeLimitSeconds").value(10))
                .andExpect(jsonPath("$.memoryLimitBytes").value(100));
    }

    @Test
    public void verifyExecutionProfileIsSaved() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();

        ExecutionProfile testExecutionProfile = new ExecutionProfile();
        testExecutionProfile.setCpuTimeLimitSeconds(10);
        testExecutionProfile.setMemoryLimitBytes(100);

        ExecutionProfile testExecutionProfileWithId = new ExecutionProfile();
        testExecutionProfileWithId.setCpuTimeLimitSeconds(10);
        testExecutionProfileWithId.setMemoryLimitBytes(100);
        testExecutionProfileWithId.setId(7);

        when(executionProfileServiceMock.save(testExecutionProfile)).thenReturn(testExecutionProfileWithId);

        // then
        mockMvc.perform(post("/executionprofile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExecutionProfile)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.cpuTimeLimitSeconds").value(10))
                .andExpect(jsonPath("$.memoryLimitBytes").value(100));
    }

}
