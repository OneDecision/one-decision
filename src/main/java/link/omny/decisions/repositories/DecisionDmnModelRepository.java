package link.omny.decisions.repositories;

import java.util.List;

import link.omny.decisions.model.dmn.DmnModel;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/decision-models")
public interface DecisionDmnModelRepository extends
        CrudRepository<DmnModel, Long> {

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.id = :id")
    DmnModel findOneForTenant(@Param("tenantId") String tenantId,
            @Param("id") Long id);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.definitionId = :definitionId")
    DmnModel findByDefinitionId(@Param("tenantId") String tenantId,
            @Param("definitionId") String definitionId);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId")
    List<DmnModel> findAllForTenant(@Param("tenantId") String tenantId);

}
