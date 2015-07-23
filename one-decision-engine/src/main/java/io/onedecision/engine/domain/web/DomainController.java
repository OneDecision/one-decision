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
package io.onedecision.engine.domain.web;

import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;
import io.onedecision.engine.domain.model.EntityField;
import io.onedecision.engine.domain.repositories.DomainModelRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to access a tenant's domain model.
 * 
 * <p>
 * The domain model, sometimes called a data dictionary, for a tenant is the
 * common language and terminology for the application and specifically for the
 * decisions embedded.
 * 
 * @author Tim Stephenson
 *
 */
@Controller
@RequestMapping(value = "/{tenantId}/domain-model")
public class DomainController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DomainController.class);

    @Autowired
    private DomainModelRepository repo;

    /**
     * @param tenantId
     *            The tenant id.
     * @return The domain model for the specified tenant or a default customer
     *         management example if the tenant is not known.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody DomainModel getModelForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Seeking domain model for tenant %1$s",
                tenantId));

        DomainModel model = repo.findByName(tenantId);
        LOGGER.debug(String.format("... result: %1$s", model));

        if (model != null) {
            LOGGER.info("... found in repository");
            return model;
        } else {
            LOGGER.info("... not found, reliant on defaults");
			return getDefaultDomain();
        }
    }

	protected DomainModel getDefaultDomain() {
        DomainModel model = new DomainModel();
		model.setName("Example customer model");
        model.setDescription("A general purpose and extensible customer model for the web");

        List<DomainEntity> entities = new ArrayList<DomainEntity>();

        // Contact
        DomainEntity entity = new DomainEntity();
        entity.setName("Contact");
        entity.setDescription("A Contact is associated with up to one Account, zero to many Notes and zero to many Documents. An Account typically has one contact though may have more.");
        entity.setImageUrl("images/domain/contact-context.png");
        List<EntityField> fields = new ArrayList<EntityField>();
        fields.add(new EntityField("firstName", "First Name",
                "Your first or given name", true, "text"));
        fields.add(new EntityField("lastName", "Last Name",
                "Your last or family name", true, "text"));
        fields.add(new EntityField("title", "Title", "Your title / salutation",
                false, "text"));
        fields.add(new EntityField("email", "Email Address",
                "Your business email address", true, "text"));
        fields.add(new EntityField("phone1", "Preferred Phone Number",
                "Your preferred telephone number", false, "tel",
                "\\+?[0-9, ]{0,13}"));
        fields.add(new EntityField("phone2", "Other Phone Number",
                "A backup telephone number", false, "tel", "\\+?[0-9, ]{0,13}"));
        fields.add(new EntityField("address1", "Address",
                "House or apartment name or number", false, "text"));
        fields.add(new EntityField("address2", "", "Stree", false, "text"));
        fields.add(new EntityField("countyOrCity", "City or County", "", false,
                "text"));
        fields.add(new EntityField("postCode", "Post Code",
				"Postal code, for example in London N1 9DH", false, "text", ""));
        fields.add(new EntityField("stage", "Stage",
                "The point in the sales funnel of this lead", false, "text"));
        fields.add(new EntityField(
                "enquiryType",
                "Enquiry Type",
                "The nature of the enquiry, typically specific to the tenant's business",
                false, "text"));
        fields.add(new EntityField("accountType", "Account Type",
                "Customer, Partner etc.", false, "text"));
        fields.add(new EntityField("owner", "Owner",
                "The sales person for this account", false, "text"));
        fields.add(new EntityField("doNotCall", "Do Not Call",
                "Is it ok to call this lead?", false, "boolean"));
        fields.add(new EntityField("doNotEmail", "Do Not Email",
                "Is it ok to email this lead?", false, "boolean"));
        fields.add(new EntityField("timeSinceEmail", "Time since email",
                "Time since our last email to contact (milliseconds)", false,
                "number"));
        fields.add(new EntityField("timeSinceLogin", "Time since login",
                "Time since last login (milliseconds)", false, "number"));
        fields.add(new EntityField("timeSinceRegistered",
                "Time since registered",
                "Time since last registered (milliseconds)", false, "number"));
        fields.add(new EntityField("tenantId", "Tenant",
				"Name of the tenant's account", true, "text"));
        fields.add(new EntityField("firstContact", "First Contact",
                "Date of first contact with this business", true, "date"));
        fields.add(new EntityField("lastUpdated", "Last Updated",
                "Date of last update to this account", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Account
        entity = new DomainEntity();
        entity.setName("Account");
        entity.setDescription("An account is associated with zero to many Notes and zero to many Documents. It typically has one Contact though may have more.");
        entity.setImageUrl("images/domain/account-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField("name", "Name",
                "Name of the company or organisation", true, "text"));
        fields.add(new EntityField(
                "companyNumber",
                "Company Number",
                "The number for this company issued by the registrar of companies in your country.",
                false, "number"));
        fields.add(new EntityField("businessWebsite", "Business Website",
                "The primary website for the business", false, "url"));
        fields.add(new EntityField("description", "Description",
                "A fuller description", false, "text"));
        fields.add(new EntityField("tenantId", "Tenant",
				"Name of the tenant's account", true, "text"));
        fields.add(new EntityField("firstContact", "First Contact",
                "Date of first contact with this business", true, "date"));
        fields.add(new EntityField("lastUpdated", "Last Updated",
                "Date of last update to this account", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Email actions
        entity = new DomainEntity();
        entity.setName("Email");
        entity.setDescription("An email is an action (conclusion) available to the decision table authors.");
        entity.setImageUrl("images/domain/email-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField("subjectLine", "Subject Line",
                "Subject line for the email", true, "text"));
        fields.add(new EntityField("templateName", "Template Name",
                "Name of email template to use", true, "text"));
        entity.setFields(fields);
        entities.add(entity);

        model.setEntities(entities);
        return model;
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param model
     *            The new model.
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public @ResponseBody void updateModelForTenant(
            @PathVariable("tenantId") String tenantId, @RequestBody DomainModel model) {
        LOGGER.info(String.format("Updating domain model for tenant %1$s",
                tenantId));
        model.setTenantId(tenantId);
        for (DomainEntity entity : model.getEntities()) {
            entity.setTenantId(tenantId);
        }
        // TODO perform checks that the model changes are not destructive.

        repo.save(model);
    }

    /**
     * Update a single entity within the tenant's model.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param model
     *            The new model.
     */
    @RequestMapping(value = "/{entity}", method = RequestMethod.PUT)
    public @ResponseBody void updateEntityForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("entity") String entityName,
            @RequestBody DomainEntity entity) {
        LOGGER.info(String.format(
                "Updating domain model for tenant %1$s with entity %2$s",
                tenantId, entityName));
        entity.setTenantId(tenantId);
        DomainModel model = getModelForTenant(tenantId);
        int idx = -1;
        for (int i = 0; i < model.getEntities().size(); i++) {
            if (entityName.equals(model.getEntities().get(i).getName())) {
                idx = i;
            }
        }
        if (idx != -1) {
            model.getEntities().remove(idx);
        }
        model.getEntities().add(entity);

        updateModelForTenant(tenantId, model);
    }

    /**
     * Deleting a model does not remove any application data, just the
     * meta-data. This can be useful to reset a clean environment after testing
     * for example.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Deleting domain model for tenant %1$s",
                tenantId));

        repo.delete(repo.findByName(tenantId));
    }
}
