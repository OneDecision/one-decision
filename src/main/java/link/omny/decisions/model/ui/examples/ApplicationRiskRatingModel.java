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
 * Example UI model to determine a health insurance application risk rating. 
 * 
 * @author Tim Stephenson
 */
@Component
public class ApplicationRiskRatingModel implements ExampleModel {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ApplicationRiskRatingModel.class);

    public DecisionModel getModel() {
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
        model.setRules(new ArrayList<DecisionExpression>());

        LOGGER.debug("... returning risk rating model");
        return model;
    }
}
