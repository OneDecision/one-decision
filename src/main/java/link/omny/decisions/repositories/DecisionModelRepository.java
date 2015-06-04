package link.omny.decisions.repositories;

import link.omny.decisions.model.ui.DecisionModel;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/decision-ui-models")
public interface DecisionModelRepository extends
        CrudRepository<DecisionModel, Long> {

    @Query("SELECT d FROM DecisionModel d WHERE d.tenantId = :tenantId AND d.name = :decisionName")
    DecisionModel findByName(@Param("tenantId") String tenantId,
            @Param("decisionName") String decisionName);

    // @Query("SELECT c FROM DecisionModel c WHERE c.tenantId = :tenantId ORDER BY c.lastUpdated DESC")
    // List<DecisionModel> findAllForTenant(@Param("tenantId") String tenantId);
}
