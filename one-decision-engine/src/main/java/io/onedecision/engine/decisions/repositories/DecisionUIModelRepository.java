package io.onedecision.engine.decisions.repositories;

import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface DecisionUIModelRepository extends
        CrudRepository<DecisionModel, Long> {

    @Query("SELECT d FROM DecisionModel d WHERE d.tenantId = :tenantId AND d.id = :id")
    DecisionModel findOneForTenant(@Param("tenantId") String tenantId,
            @Param("id") Long id);

    @Query("SELECT d FROM DecisionModel d WHERE d.tenantId = :tenantId AND d.name = :decisionName")
    DecisionModel findByName(@Param("tenantId") String tenantId,
            @Param("decisionName") String decisionName);

    @Query("SELECT c FROM DecisionModel c WHERE c.tenantId = :tenantId ORDER BY c.lastUpdated DESC")
    List<DecisionModel> findAllForTenant(@Param("tenantId") String tenantId);
}
