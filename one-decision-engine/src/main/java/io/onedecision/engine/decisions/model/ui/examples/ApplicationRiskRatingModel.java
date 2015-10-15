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
package io.onedecision.engine.decisions.model.ui.examples;

import io.onedecision.engine.decisions.model.ui.DecisionConclusion;
import io.onedecision.engine.decisions.model.ui.DecisionCondition;
import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.util.ArrayList;
import java.util.List;

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

        List<DecisionCondition> conditions = new ArrayList<DecisionCondition>();
        conditions.add(new DecisionCondition("Applicant Age", new String[] {
                "<25", "<25", "[25..60]", ">60", ">60" }));
        conditions.add(new DecisionCondition("Medical History", new String[] {
                "good", "bad", "-", "good", "bad" }));
        model.setConditions(conditions);

        List<DecisionConclusion> conclusions = new ArrayList<DecisionConclusion>();
        conclusions.add(new DecisionConclusion("Low", new String[] { "X", "-",
                "-", "-", "-" }));
        conclusions.add(new DecisionConclusion("Medium", new String[] { "-",
                "X", "X", "X", "-" }));
        conclusions.add(new DecisionConclusion("High", new String[] { "-", "-",
                "-", "-", "X" }));
        model.setConclusions(conclusions);
        // model.setRules(new ArrayList<DecisionExpression>());

        LOGGER.debug("... returning risk rating model");
        return model;
    }
}
