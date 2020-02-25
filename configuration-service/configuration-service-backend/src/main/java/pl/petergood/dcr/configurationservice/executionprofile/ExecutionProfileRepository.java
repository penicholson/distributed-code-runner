package pl.petergood.dcr.configurationservice.executionprofile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ExecutionProfileRepository extends CrudRepository<ExecutionProfile, Integer> {
}
