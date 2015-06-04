package link.omny.decisions.web;

import java.util.ArrayList;
import java.util.List;

import link.omny.decisions.model.ui.DecisionExpression;
import link.omny.decisions.model.ui.DecisionModel;
import link.omny.decisions.repositories.DecisionModelRepository;

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
 * Controller to support the decision definition UI.
 * 
 * <p>
 * For execution and interchange decision models are stored as DMN models.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/decision-ui-models")
public class DecisionUIModelController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionUIModelController.class);

    @Autowired
    private DecisionModelRepository repo;

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody Iterable<DecisionModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        // List<DecisionModel> list = repo.findAllForTenant(tenantId);
        Iterable<DecisionModel> list = repo.findAll();
        // LOGGER.info(String.format("Found %1$s decision models",
        // list.size()));

        return list;
    }

    @RequestMapping(value = "/{decisionName}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DecisionModel getModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("decisionName") String decisionName) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", decisionName,
                tenantId));

        if (repo != null) {
            DecisionModel model = repo.findByName(tenantId, decisionName);
            LOGGER.debug(String.format("... result from db: %1$s", model));

            if (model != null) {
                LOGGER.info("... found in repository");
                return model;
            }
        }

        switch (tenantId) {
        case "firmgains":
            switch (decisionName) {
            case "email":
                return getFirmGainsEmailFollowUpModel();
            case "valuation":
                return getFirmGainsValuationModel();
            default:
                return getNewDecisionModel();
            }
        case "omny":
            switch (decisionName) {
            case "riskRating":
                return getRiskRatingModel();
            default:
                return getNewDecisionModel();
            }
        case "trakeo":
            switch (decisionName) {
            case "sustainabilityRanking":
                return getSustainabilityRankingModel();
            default:
                return getNewDecisionModel();
            }
        default:
            switch (decisionName) {
            case "email":
                return getEmailFollowUpModel();
            default:
                return getNewDecisionModel();
            }
        }
    }

    private DecisionModel getRiskRatingModel() {
        DecisionModel model = new DecisionModel();
        model.setName("Applicant Risk Rating");

        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("Applicant Age", new String[] {
                "<25", "<25", "[25..60]", ">60", ">60" }));
        conditions.add(new DecisionExpression("Medical History", new String[] {
                "good", "bad", "-", "good", "bad" }));
        model.setConditions(conditions);

        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions.add(new DecisionExpression("Low", new String[] { "X", "-",
                "-", "-", "-" }));
        conclusions.add(new DecisionExpression("Medium", new String[] { "-",
                "X", "X", "X", "-" }));
        conclusions.add(new DecisionExpression("High", new String[] { "-", "-",
                "-", "-", "X" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning risk rating model");
        return model;
    }

    private DecisionModel getNewDecisionModel() {
        DecisionModel model = new DecisionModel();
        model.setName("A new decision");
        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("a new condition",
                new String[] { "-" }));
        model.setConditions(conditions);
        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions.add(new DecisionExpression("a new conclusion",
                new String[] { "-" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning new (empty) model");
        return model;
    }

    private DecisionModel getEmailFollowUpModel() {
        DecisionModel model = new DecisionModel();
        model.setName("Personal Follow-Up");

        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("Since last email", new String[] {
                "< 7d", ">= 7d", "", "", "", "", "", "", "", "" }));
        conditions.add(new DecisionExpression("Since registration",
                new String[] { "", "", ">= 2w", ">= 6w", ">= 10 weeks",
                        ">= 52 weeks", "", "" }));
        conditions.add(new DecisionExpression("Since login",
                new String[] { "", "", "", "", "", "", ">= 4 weeks",
                        ">= 12 weeks", ">= 24 weeks" }));
        conditions.add(new DecisionExpression("Not yet sent",
                new String[] { "discover", "intro-services",
                        "business-sale-ideas", "anniversary whats-on",
                        "is-there-progress", "need-a-hand", "" }));
        // conditions.add(new DecisionExpression("Otherwise", new String[] { "",
        // "", "", "", "", "", "", "", "", "", "", "", "", "", "true" }));
        model.setConditions(conditions);

        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions
                .add(new DecisionExpression("Template to use", new String[] {
                        "discover-firmgains", "intro-services",
                        "business-sale-ideas", "anniversary", "whats-on",
                        "is-there-progress", "need-a-hand" }));
        conclusions.add(new DecisionExpression("Subject Line", new String[] {
                "Get Your Business Sale Plans into Action (not inaction!)",
                "Are you Fully Equipped for Your Business Sale?",
                "There’s More Under the Surface with Firm Gains",
                "Every Business Owner Needs a Helping Hand",
                "What Makes a ‘Good’ Business Sale?",
                "A Very Happy Anniversary… We Hope!",
                "Psst… Here’s a couple of nuggets for you from Firm Gains",
                "We hope your business sale is progressing well",
                "Where have you got to?" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning email follow up model");
        return model;
    }

    private DecisionModel getFirmGainsEmailFollowUpModel() {
        DecisionModel model = getEmailFollowUpModel();
        model.setName("Firm Gains Email Follow-Up");
        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("Since last email", new String[] {
                "< 7d", "< 7d", ">= 7d", "", "", "", "", "", "", "", "", "",
                "", "", "" }));
        conditions.add(new DecisionExpression("Since valuation",
                new String[] { "1 hour < x < 1 week", "", ">= 1 week",
                        ">= 4 weeks", ">= 4 weeks", ">= 4 weeks", "", "", "",
                        "", "", "", "", "", "" }));
        conditions.add(new DecisionExpression("Mid-range quote", new String[] {
                "", "", "", "< 100K", "100K <= valuation < 600K", ">= 600K",
                "", "", "", "", "", "", "", "", "" }));
        conditions.add(new DecisionExpression("Since business plan download",
                new String[] { "", "", "", "", "", "", ">= 1 week",
                        ">= 5 weeks", "", "", "", "", "", "", "" }));
        conditions.add(new DecisionExpression("Since registration",
                new String[] { "", "", "", "", "", "", "", "", ">= 2w",
                        ">= 6w", ">= 10 weeks", ">= 52 weeks", "", "", "" }));
        conditions.add(new DecisionExpression("Since login", new String[] { "",
                "", "", "", "", "", "", "", "", "", "", "", ">= 4 weeks",
                ">= 12 weeks", ">= 24 weeks" }));
        conditions.add(new DecisionExpression("Not yet sent",
                new String[] { "valuation-detail", "", "low-valuation-email",
                        "mid-valuation-email", "high-valuation-email",
                        "valuation-advice", "plan-help", "plan-next",
                        "discover-firmgains", "intro-services",
                        "business-sale-ideas", "anniversary whats-on",
                        "is-there-progress", "need-a-hand", "" }));
        // conditions.add(new DecisionExpression("Otherwise", new String[] { "",
        // "", "", "", "", "", "", "", "", "", "", "", "", "", "true" }));
        model.setConditions(conditions);

        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions
                .add(new DecisionExpression("Template to use", new String[] {
                        "valuation-detail", "", "low-valuation-email",
                        "mid-valuation-email", "high-valuation-email",
                        "valuation-advice", "plan-help", "plan-next",
                        "discover-firmgains", "intro-services",
                        "business-sale-ideas", "anniversary", "whats-on",
                        "is-there-progress", "need-a-hand" }));
        conclusions.add(new DecisionExpression("Subject Line", new String[] {
                "About Your Business Valuation: Reasonable or Risible?", "",
                "We’ve taken a close look at your Firm Gains valuation",
                "We’ve taken a close look at your Firm Gains valuation",
                "We’ve taken a close look at your Firm Gains valuation",
                "Why Your Valuation Means Nothing…",
                "Get Your Business Sale Plans into Action (not inaction!)",
                "Are you Fully Equipped for Your Business Sale?",
                "There’s More Under the Surface with Firm Gains",
                "Every Business Owner Needs a Helping Hand",
                "What Makes a ‘Good’ Business Sale?",
                "A Very Happy Anniversary… We Hope!",
                "Psst… Here’s a couple of nuggets for you from Firm Gains",
                "We hope your business sale is progressing well",
                "Where have you got to?" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning firmgains email follow up model");
        return model;
    }

    private DecisionModel getFirmGainsValuationModel() {
        DecisionModel model = new DecisionModel();
        model.setName("Simplified Business Valuation");

        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("EBITDA", new String[] {
                "<50000", "50000 <= ebitda < 300000",
                "300000 <= ebitda < 1000000", ">= 1000000" }));
        model.setConditions(conditions);

        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions.add(new DecisionExpression("Low Quote Multiple",
                new String[] { "1.45", "1.95", "2.5", "3.1" }));
        conclusions.add(new DecisionExpression("Medium Quote Multiple",
                new String[] { "2", "2.9", "3.75", "4.95" }));
        conclusions.add(new DecisionExpression("High Quote Multiple",
                new String[] { "2.65", "3.85", "5.5", "6.85" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning firmgains valuation model");
        return model;
    }

    private DecisionModel getSustainabilityRankingModel() {
        DecisionModel model = new DecisionModel();
        model.setName("Sustainability Scorecard");

        List<DecisionExpression> conditions = new ArrayList<DecisionExpression>();
        conditions.add(new DecisionExpression("Applicant Age", new String[] {
                "<25", "<25", "[25..60]", ">60", ">60" }));
        conditions.add(new DecisionExpression("Medical History", new String[] {
                "good", "bad", "-", "good", "bad" }));
        model.setConditions(conditions);

        List<DecisionExpression> conclusions = new ArrayList<DecisionExpression>();
        conclusions.add(new DecisionExpression("Low", new String[] { "X", "-",
                "-", "-", "-" }));
        conclusions.add(new DecisionExpression("Medium", new String[] { "-",
                "X", "X", "X", "-" }));
        conclusions.add(new DecisionExpression("High", new String[] { "-", "-",
                "-", "-", "X" }));
        model.setConclusions(conclusions);

        LOGGER.debug("... returning sustainability scorecard model");
        return model;
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant to create the model for.
     * @param model
     *            The new model.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody void createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody DecisionModel model) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                tenantId));

        // TODO perform checks that the model changes are not destructive.

        repo.save(model);
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param model
     *            The updated model.
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public @ResponseBody void updateModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("decisionName") String decisionName,
            @RequestBody DecisionModel model) {
        LOGGER.info(String.format(
                "Updating decision model %2$s for tenant %1$s", tenantId,
                decisionName));

        // TODO perform checks that the model changes are not destructive.

        repo.save(model);
    }

    /**
     * Delete the named model for the tenant.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     * @param decisionName
     *            Id of a particular decision.
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("decisionName") String decisionName) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", decisionName,
                tenantId));

        repo.delete(repo.findByName(tenantId, decisionName));
    }
}
