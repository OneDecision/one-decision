package link.omny.domain.web;

import java.util.ArrayList;
import java.util.List;

import link.omny.domain.model.CustomEntityField;
import link.omny.domain.model.DomainEntity;
import link.omny.domain.model.DomainModel;
import link.omny.domain.model.EntityField;
import link.omny.domain.repositories.DomainModelRepository;

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
        }

        switch (tenantId) {
        case "firmgains":
            LOGGER.info("... returning firmgains");
            return getFirmGainsDomain();
        case "omny":
            LOGGER.info("... returning omny");
            return getOmnyDomain();
        case "trakeo":
            LOGGER.info("... returning trakeo");
            return getTrakeoDomain();
        default:
            LOGGER.info("... returning vanilla default");
            return getDefaultDomain();
        }
    }

    private DomainModel getDefaultDomain() {
        DomainModel model = new DomainModel();
        model.setName("Omny Link customer model");
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
                "Postal code, example N1 9DH", false, "text", ""));
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
        fields.add(new EntityField(
                "source",
                "Source",
                "Where this lead came from, typically auto-populated by pay-per-click system",
                false, "text"));
        fields.add(new EntityField(
                "medium",
                "Medium",
                "Medium this lead came via, typically auto-populated by pay-per-click system",
                false, "text"));
        fields.add(new EntityField("campaign", "Campaign",
                "Additional information from the pay-per-click system", false,
                "text"));
        fields.add(new EntityField("keyword", "Keyword",
                "Additional information from the pay-per-click system", false,
                "text"));
        fields.add(new EntityField("timeSinceEmail", "Time since email",
                "Time since our last email to contact (milliseconds)", false,
                "number"));
        fields.add(new EntityField("timeSinceLogin", "Time since login",
                "Time since last login (milliseconds)", false, "number"));
        fields.add(new EntityField("timeSinceRegistered",
                "Time since registered",
                "Time since last registered (milliseconds)", false, "number"));
        fields.add(new EntityField("tenantId", "Tenant",
                "Name of the the Omny account", true, "text"));
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
        fields.add(new EntityField("aliases", "Also known as",
                "Other names for the company such as names you trade as",
                false, "text"));
        fields.add(new EntityField("businessWebsite", "Business Website",
                "The primary website for the business", false, "url"));
        fields.add(new EntityField("shortDesc", "Short Description",
                "Brief description of the business", false, "text"));
        fields.add(new EntityField("description", "Description",
                "A fuller description", false, "text"));
        fields.add(new EntityField("incorporationYear", "Established In",
                "The year the business was incorporated", false, "number"));
        fields.add(new EntityField("noOfEmployees", "No. of Employees",
                "The number of full time staff you employee", false, "number"));
        fields.add(new EntityField("tenantId", "Tenant",
                "Name of the the Omny account", true, "text"));
        fields.add(new EntityField("firstContact", "First Contact",
                "Date of first contact with this business", true, "date"));
        fields.add(new EntityField("lastUpdated", "Last Updated",
                "Date of last update to this account", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Activity
        entity = new DomainEntity();
        entity.setName("Activity");
        entity.setDescription("An Activity is associated with exactly one Contact.");
        entity.setImageUrl("images/domain/activity-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField(
                "type", "Type",
                "Type of activity, for example: register, login, download etc.",
                false, "text"));
        fields.add(new EntityField(
                "content", "Content",
                "Additional content dependent on the type, for example for a download this will hold what was downloaded",
                true, "text"));
        fields.add(new EntityField("occurred", "Occurred",
                "Date and time this activity occurred", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Note
        entity = new DomainEntity();
        entity.setName("Note");
        entity.setDescription("A note is associated with exactly one Contact.");
        entity.setImageUrl("images/domain/note-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField("author", "Author",
                "Name / username of the person creating this note", true,
                "text"));
        fields.add(new EntityField("content", "Content",
                "The body of the note.", false, "text"));
        fields.add(new EntityField("created", "Created",
                "Date this note was entered", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Document
        entity = new DomainEntity();
        entity.setName("Document");
        entity.setDescription("A document is associated with exactly one Contact.");
        entity.setImageUrl("images/domain/document-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField("author", "Author",
                "Name / username of the person creating this document", true,
                "text"));
        fields.add(new EntityField("url", "URL",
                "The location of the document.", false, "text"));
        fields.add(new EntityField("created", "Created",
                "Date this document was entered", true, "date"));
        entity.setFields(fields);
        entities.add(entity);

        // Users
        entity = new DomainEntity();
        entity.setName("User");
        entity.setDescription("A User of the system who may belong to one or more groups and who may be allocated work.");
        entity.setImageUrl("images/domain/user-context.png");
        fields = new ArrayList<EntityField>();
        fields.add(new EntityField("firstName", "First Name",
                "Your first or given name", true, "text"));
        fields.add(new EntityField("lastName", "Last Name",
                "Your last or family name", true, "text"));
        fields.add(new EntityField("email", "Email Address",
                "Your business email address", true, "text"));
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

    private DomainModel getFirmGainsDomain() {
        DomainModel model = getDefaultDomain();

        // Contact
        List<EntityField> fields = model.getEntities().get(0).getFields();
        fields.add(new CustomEntityField("shareOfBusiness",
                "Share of Business (%)", "Your share in the business.", false,
                "number"));

        // Account
        fields = model.getEntities().get(1).getFields();
        fields.add(new CustomEntityField("alreadyContacted",
                "Already Contacted",
                "Any brokers or purchasers you are already in contact with",
                false, "text"));
        fields.add(new CustomEntityField("ebitda", "EBITDA",
                "Earnings before interest, tax, debts and adjustments", false,
                "number"));
        fields.add(new CustomEntityField("surplus", "Surplus",
                "Surplus cash and investments", false, "number"));
        fields.add(new CustomEntityField("depreciationAmortisation",
                "Depreciation / Amortisation", "", false, "number"));
        fields.add(new CustomEntityField("operatingProfit", "Operating Profit",
                "Gross profits", false, "number"));
        fields.add(new CustomEntityField("adjustments", "Adjustments", "",
                false, "number"));
        fields.add(new CustomEntityField("borrowing", "Borrowing", "", false,
                "number"));
        fields.add(new CustomEntityField("lowQuote", "Low quote", "", false,
                "number"));
        fields.add(new CustomEntityField("mediumQuote", "Medium quote", "",
                false, "number"));
        fields.add(new CustomEntityField("highQuote", "High quote", "", false,
                "number"));
        fields.add(new CustomEntityField("askingPrice", "Asking price", "",
                false, "number"));

        return model;
    }

    private DomainModel getOmnyDomain() {
        DomainModel model = getDefaultDomain();

        // Contact
        List<EntityField> fields = model.getEntities().get(0).getFields();
        fields.add(new CustomEntityField("age", "Age",
                "Your age last birthday.", false,
                "number"));
        fields.add(new CustomEntityField("health", "Health",
                "Your general state of health.", false, "text"));
        fields.add(new CustomEntityField("riskRating", "Risk Rating",
                "Risk rating calculated for you.", false, "text"));
        return model;
    }

    private DomainModel getTrakeoDomain() {
        DomainModel model = getDefaultDomain();

        // Sustainability
        DomainEntity entity = new DomainEntity();
        entity.setName("Scorecard");
        entity.setDescription("A Scorecard holds various KPIs used to produce a balanced scorecard of an Account's sustainability. Each Account has exactly one Scorecard");
        entity.setImageUrl("images/domain/user-context.png");
        List<EntityField> fields = new ArrayList<EntityField>();

        fields.add(new EntityField("disclosureDate", "Disclosure Date",
                "Date of last disclosure", false, "date"));
        fields.add(new EntityField("score", "Score",
                "Sustainability Score", false, "number"));
        fields.add(new EntityField("eClasses", "EClasses",
                "EClasses supplied", false, "text"));
        fields.add(new EntityField(
                "co2Intensity",
                "CO2 Intensity",
                "The ratio of carbon footprint to mometory value of goods supplied",
                false, "number"));
        fields.add(new EntityField("certifications", "Certifications",
                "Relevant certifications held", false, "text"));
        fields.add(new EntityField("carbonAccounting", "Carbon Accounting",
                "Degree of Carbon Accounting undertaken",
                false, "text"));
        fields.add(new EntityField("supplierMemberOfCdp",
                "Supplier Member of CDP?",
                "Is the organisation a Supplier Member of CDP?", false,
                "boolean"));
        fields.add(new EntityField("signatoryToCdp", "Signatory To CDP?",
                "Is the organisation a signatory to CDP?", false, "boolean"));
        fields.add(new EntityField("sustainabilityPolicy",
                "Sustainability Policy in place?",
                "Is there a written Sustainability Policy for the organisation?",
                false, "boolean"));
        fields.add(new EntityField("useSpotContracts",
                "Use of spot contracts?",
                "Does the organisation make use of spot contracts?", false,
                "boolean"));
        fields.add(new EntityField("useSubcontractors",
                "Use of subcontractors?",
                "Does the organisation make use of sub-contractors?", false,
                "boolean"));
        fields.add(new EntityField("tradeBodiesSubscribedTo",
                "Trade Bodies Subscribed To",
                "Please list any trade bodies subscribed to", false, "text"));
        fields.add(new EntityField("namedPersonResponsibleForEthics",
                "Named Person Responsible for Ethics?",
                "Is there a named person responsible for ethics?", false,
                "boolean"));
        fields.add(new EntityField("staffEthicalDevelopment",
                "Staff Ethical Development Policy?",
                "Is there a staff ethical development policy in place?", false,
                "boolean"));
        fields.add(new EntityField("upstreamCodeOfConductAudit",
                "Upstream Code of Conduct Audited?",
                "Is there a code of conduct for upstream suppliers and is it audited?",
                false, "boolean"));
        fields.add(new EntityField("operatingCountries", "Operating Countries",
                "List of countries the organisation has operations in", false,
                "text"));
        fields.add(new EntityField(
                "localBusiness",
                "'Local' Operations",
                "The amount of business operations carried out locally to the Hub partner",
                false, "text"));
        fields.add(new EntityField(
                "primaryMaterial",
                "Primary Material",
                "The primary input material (or materials if more than one product supplied)",
                false, "text"));
        fields.add(new EntityField("secondaryMaterial", "Secondary Material",
                "Other signifiant input materials", false, "text"));
        fields.add(new EntityField(
                "watchListMaterials",
                "Watch List Materials",
                "Does the organisation use any amount of the Watch List materials? If so, specify",
                false, "text"));
        fields.add(new EntityField(
                "convictions",
                "Convictions under Environmental Legislation",
                "Does the organisation have any actual convictions or pending actions under environmental legislation? If so, specify",
                false, "text"));
        fields.add(new EntityField(
                "airQuality",
                "Air Quality Awareness",
                "What level of awareness exists of air quality impacts of the organisation",
                false, "text"));
        entity.setFields(fields);
        model.getEntities().add(2, entity);

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
