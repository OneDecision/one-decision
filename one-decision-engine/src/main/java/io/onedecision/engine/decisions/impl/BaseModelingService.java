package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionIllegalArgumentException;
import io.onedecision.engine.decisions.api.DecisionNotFoundException;
import io.onedecision.engine.decisions.api.ModelingService;
import io.onedecision.engine.decisions.converter.DecisionModelConverter;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.domain.api.DomainModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseModelingService implements ModelingService {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(BaseModelingService.class);

    private DecisionModelConverter converter = new DecisionModelConverter();

    @Autowired
    @Qualifier("halObjectMapper")
    private ObjectMapper mapper;

    protected List<DecisionModel> repo;

    private DomainModelFactory domainModelFactory;

    protected ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    protected void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<DecisionModel> listForTenant(String tenantId) {
        ArrayList<DecisionModel> tenantModels = new ArrayList<DecisionModel>();
        for (DecisionModel model : repo) {
            if (tenantId.equals(model.getTenantId())) {
                tenantModels.add(model);
            }
        }
        return tenantModels;
    }

    @Override
    public DecisionModel getModelForTenant(Long id, String tenantId) {
        for (DecisionModel model : repo) {
            if (tenantId.equals(model.getTenantId())
                    && id.equals(model.getId())) {
                return model;
            }
        }
        return null;
    }

    @Override
    public DecisionModel createModelForTenant(DecisionModel model,
            String tenantId) {
        model.setTenantId(tenantId); // precaution
        repo.add(model);
        return model;
    }

    @Override
    public DecisionModel updateModelForTenant(Long uid, DecisionModel model,
            String tenantId) {
        model.setId(uid);
        return createModelForTenant(model, tenantId);
    }

    @Override
    public void deleteModelForTenant(Long id, String tenantId) {
        DecisionModel model = getModelForTenant(id, tenantId);
        if (model == null) {
            throw new DecisionNotFoundException(String.format(
                    "Unable to find model for tenant %1$s with id %2$d",
                    tenantId, id));
        }
        repo.remove(model);
    }

    @Override
    public void setDomainModelFactory(DomainModelFactory domainModelFactory) {
        this.domainModelFactory = domainModelFactory;
    }

    @Override
    public Definitions convert(DecisionModel source) {
        if (domainModelFactory != null) {
            converter.setDomainModelFactory(domainModelFactory);
        }
        return converter.convert(source);
    }

    @Override
    public Definitions convert(String source) {
        DecisionModel jsonModel;
        try {
            jsonModel = getMapper().readValue(source, DecisionModel.class);
            return convert(jsonModel);
        } catch (IOException e) {
            String msg = "Unable to parse decision model from json";
            LOGGER.error(msg);
            throw new DecisionIllegalArgumentException(msg, e);
        }
    }

}
