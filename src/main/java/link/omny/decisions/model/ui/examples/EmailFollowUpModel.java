package link.omny.decisions.model.ui.examples;

import java.util.ArrayList;
import java.util.List;

import link.omny.decisions.model.ui.DecisionExpression;
import link.omny.decisions.model.ui.DecisionModel;
import link.omny.decisions.model.ui.ExampleModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Example UI model to decide which email to send a prospect. 
 * 
 * @author Tim Stephenson
 */
@Component
public class EmailFollowUpModel implements ExampleModel {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EmailFollowUpModel.class);

    public DecisionModel getModel() {
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
}
