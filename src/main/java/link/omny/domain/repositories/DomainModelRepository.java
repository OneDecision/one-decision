package link.omny.domain.repositories;

import link.omny.domain.model.DomainModel;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/domain-models")
public interface DomainModelRepository extends
        CrudRepository<DomainModel, Long> {

    @Query("SELECT d FROM DomainModel d WHERE d.tenantId = :tenantId")
    DomainModel findByName(@Param("tenantId") String tenantId);
}
