package pl.petergood.dcr.configurationservice.executionprofile;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("/executionprofile")
public class ExecutionProfileController {

    private ExecutionProfileService executionProfileService;

    public ExecutionProfileController(ExecutionProfileService executionProfileService) {
        this.executionProfileService = executionProfileService;
    }

    @GetMapping("/{id}")
    public ExecutionProfile getExecutionProfile(@PathVariable("id") int id) {
        try {
            return executionProfileService.findById(id);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given execution profile does not exists.", ex);
        }
    }

    @PostMapping
    public ExecutionProfile createExecutionProfile(@RequestBody ExecutionProfile executionProfile) {
        try {
            return executionProfileService.save(executionProfile);
        } catch (TransactionSystemException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The given execution profile violates constraints", ex);
        }
    }

}
