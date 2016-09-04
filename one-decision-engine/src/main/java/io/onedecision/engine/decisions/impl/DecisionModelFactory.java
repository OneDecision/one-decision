/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.api.DecisionNotFoundException;
import io.onedecision.engine.decisions.api.RepositoryService;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DecisionModelFactory implements DecisionConstants,
        RepositoryService {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionModelFactory.class);

    protected List<DmnModel> repo;

    private Validator validator;

    public DecisionModelFactory() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        repo = new ArrayList<DmnModel>();
    }

    @Override
    public void write(Definitions dm, Writer out) throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Since no @XmlRootElement generated for Definitions need to create
            // element wrapper here. See
            // https://weblogs.java.net/blog/kohsuke/archive/2006/03/why_does_jaxb_p.html
            m.marshal(new JAXBElement<Definitions>(DEFINITIONS,
                    Definitions.class, dm), out);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public final Definitions loadFromClassPath(String resourceName)
            throws IOException {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(resourceName);
            return load(is);
        } catch (IOException e) {
            String msg = "Unable to load decision model from " + resourceName;
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected final Definitions load(InputStream inputStream)
            throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Unmarshaller um = context.createUnmarshaller();

            Object dm = um.unmarshal(inputStream);
            if (dm instanceof JAXBElement<?>) {
                return ((JAXBElement<Definitions>) dm).getValue();
            } else {
                return (Definitions) dm;
            }
        } catch (JAXBException e) {
            String msg = "Unable to load decision model from stream";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        }
    }

    @Override
    public List<DmnModel> listForTenant(@NotNull String tenantId) {
        ArrayList<DmnModel> tenantModels = new ArrayList<DmnModel>();
        for (DmnModel dmnModel : repo) {
            if (tenantId.equals(dmnModel.getTenantId())) {
                tenantModels.add(dmnModel);
            }
        }
        return tenantModels;
    }

    protected DmnModel getModelForTenant(Long id, String tenantId) {
        for (DmnModel dmnModel : repo) {
            if (tenantId.equals(dmnModel.getTenantId())
                    && id.equals(dmnModel.getShortId())) {
                return dmnModel;
            }
        }
        return null;
    }

    @Override
    public DmnModel getModelForTenant(String definitionId, String tenantId) {
        for (DmnModel dmnModel : repo) {
            if (tenantId.equals(dmnModel.getTenantId())
                    && definitionId.equals(dmnModel.getDefinitionId())) {
                return dmnModel;
            }
        }
        throw new DecisionNotFoundException(String.format(
                "Could not find decision model with definition %1$s for tenant %2$s",
                definitionId, tenantId));
    }

    @Override
    public String getDmnForTenant(String definitionId, String tenantId) {
        return getModelForTenant(definitionId, tenantId).getDefinitionXml();
    }

    @Override
    public byte[] getImageForTenant(String definitionId, String tenantId) {
        return getModelForTenant(definitionId, tenantId).getImage();
    }

    @Override
    public DmnModel createModelForTenant(DmnModel model) {
        repo.add(model);
        return model;
    }

    @Override
    public void updateModelForTenant(String definitionId, DmnModel model,
            String tenantId) {
        // be sure
        model.setTenantId(tenantId);
        model.setDefinitionId(definitionId);
        createModelForTenant(model);
    }

    @Override
    public void deleteModelForTenant(Long id, String tenantId) {
        DmnModel model = getModelForTenant(id, tenantId);
        if (model == null ) { 
            throw new DecisionNotFoundException(String.format(
                    "Unable to find model for tenant %1$s with id %2$d",
                    tenantId, id));
        }
        if (!repo.remove(model)) {
            LOGGER.error(String.format(
                    "Unable to delete model for tenant %1$s with id %2$d",
                    tenantId, id));
        }
    }

    @Override
    public void deleteModelForTenant(String definitionId, String tenantId) {
        DmnModel model = getModelForTenant(definitionId, tenantId);
        if (model == null) {
            throw new DecisionNotFoundException(String.format(
                    "Unable to find model for tenant %1$s with id %2$s",
                    tenantId, definitionId));
        }
        if (!repo.remove(model)) {
            LOGGER.error(String.format(
                    "Unable to delete model for tenant %1$s with id %2$s",
                    tenantId, definitionId));
        }
    }

    @Override
    public Set<ConstraintViolation<Definitions>> validate(Definitions def)
            throws IOException {
        StringWriter sw = new StringWriter();
        write(def, sw);

        ByteArrayInputStream is = null;
        Set<ConstraintViolation<Definitions>> violations = null;
        try {
            is = new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
            SchemaValidator schemaValidator = new SchemaValidator();
            violations = schemaValidator.validate(is);
            if (violations.isEmpty()) {
                violations = validator.validate(def);
            }
        } finally {
            is.close();
        }

        return violations;
    }

}
