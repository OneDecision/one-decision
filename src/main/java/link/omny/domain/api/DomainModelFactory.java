package link.omny.domain.api;

import link.omny.domain.model.DomainModel;

public interface DomainModelFactory {

    DomainModel fetchDomain(String domainModelUri);
}
