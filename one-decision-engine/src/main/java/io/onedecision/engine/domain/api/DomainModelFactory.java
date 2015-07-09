package io.onedecision.engine.domain.api;

import io.onedecision.engine.domain.model.DomainModel;

public interface DomainModelFactory {

    DomainModel fetchDomain(String domainModelUri);
}
