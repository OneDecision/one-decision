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
package io.onedecision.engine.decisions.web;

import io.onedecision.engine.decisions.api.ModelingService;
import io.onedecision.engine.decisions.impl.BaseModelingService;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.decisions.model.ui.examples.ApplicationRiskRatingModel;
import io.onedecision.engine.decisions.model.ui.examples.ExampleModel;
import io.onedecision.engine.decisions.repositories.DecisionUIModelRepository;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller to support the decision definition UI.
 * 
 * <p>
 * For execution and interchange decision models are stored as DMN models.
 * 
 * @author Tim Stephenson
 * @deprecated Since 1.2 focus on DMN models as there are now plenty of modeling
 *             tools.
 */
@Controller
@RequestMapping(value = "/{tenantId}/decision-ui-models")
public class DecisionUIModelController extends BaseModelingService implements
        ModelingService {

    @Autowired
    private DecisionUIModelRepository repo;

    private List<ExampleModel> examples;

    /**
     * Install example decision ui models.
     * 
     * @param tenantId
     */
    @RequestMapping(value = "/installExamples", method = RequestMethod.GET)
    public @ResponseBody void installExamples(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        for (ExampleModel ex : getExampleUIModels()) {
            createModelForTenant(ex.getModel(), tenantId);
        }
    }

	protected List<ExampleModel> getExampleUIModels() {
        if (examples == null) {
            examples = new ArrayList<ExampleModel>();
            examples.add(new ApplicationRiskRatingModel());
        }
        return examples;
    }

    /* (non-Javadoc)
     * @see io.onedecision.engine.decisions.web.ModelingService#listForTenant(java.lang.String)
     */
    @Override
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<DecisionModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        List<DecisionModel> list = repo.findAllForTenant(tenantId);
        LOGGER.info(String.format("Found %1$s decision ui models", list.size()));

        return list;
    }

    /* (non-Javadoc)
     * @see io.onedecision.engine.decisions.web.ModelingService#getModelForTenant(java.lang.String, java.lang.Long)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DecisionModel getModelForTenant(
            @PathVariable("id") Long id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id,
                tenantId));

        DecisionModel model = repo.findOneForTenant(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        if (model != null) {
            LOGGER.info("... found in repository");
        }
        return model;
    }


    /* (non-Javadoc)
     * @see io.onedecision.engine.decisions.web.ModelingService#createModelForTenant(java.lang.String, io.onedecision.engine.decisions.model.ui.DecisionModel)
     */
    @Override
    public @ResponseBody DecisionModel createModelForTenant(
            @RequestBody DecisionModel model,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                tenantId));

        model.setTenantId(tenantId);
        // TODO perform checks that the model changes are not destructive.

        return repo.save(model);
    }

	/*
	 * TODO this method is needed to return the expected Location header until
	 * such time as full Hateos is implemented. Please do not rely on it.
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody DecisionModel createModelForTenant(
			@PathVariable("tenantId") String tenantId,
			@RequestBody DecisionModel model, 
			HttpServletRequest request, HttpServletResponse response) {
		DecisionModel dm= createModelForTenant(model,
				tenantId);
		response.setHeader("Location",
				String.format("%1$s%2$d", request.getRequestURL(), dm.getId()));
		return dm;
	}

	/* (non-Javadoc)
     * @see io.onedecision.engine.decisions.web.ModelingService#updateModelForTenant(java.lang.String, java.lang.Long, io.onedecision.engine.decisions.model.ui.DecisionModel)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody DecisionModel updateModelForTenant(
            @PathVariable("id") Long id,
            @RequestBody DecisionModel model,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Updating decision model %2$s for tenant %1$s", tenantId, id));

        // id may be null depending on the configuration of
        if (model.getId() != null && !id.equals(model.getId())) {
            throw new IllegalStateException(
                    "Proposed model does not match the resource identifier");
        }
        // TODO perform checks that the model changes are not destructive.

        return repo.save(model);
    }

    /* (non-Javadoc)
     * @see io.onedecision.engine.decisions.web.ModelingService#deleteModelForTenant(java.lang.String, java.lang.Long)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("id") Long id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", id,
                tenantId));

        repo.delete(id);
    }

}
