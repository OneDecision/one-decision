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

import io.onedecision.engine.decisions.model.ui.DecisionInput;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.decisions.model.ui.DecisionOutput;
import io.onedecision.engine.decisions.model.ui.DecisionRule;

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

        List<DecisionInput> inputs = new ArrayList<DecisionInput>();
        // WRONG: not the info item but the allowed values
        inputs.add(new DecisionInput().withName("Applicant Age"));
        inputs.add(new DecisionInput().withName("Medical History"));
        model.setInputs(inputs);

        List<DecisionOutput> outputs = new ArrayList<DecisionOutput>();
        outputs.add(new DecisionOutput().withName("Low"));
        outputs.add(new DecisionOutput().withName("Medium"));
        outputs.add(new DecisionOutput().withName("High"));
        model.setOutputs(outputs);

        ArrayList<DecisionRule> rules = new ArrayList<DecisionRule>();
        rules.add(new DecisionRule().withInputEntries(new String[] { "<25",
                "good", "Low" }));
        rules.add(new DecisionRule().withInputEntries(new String[] { "<25",
                "bad", "Medium" }));
        rules.add(new DecisionRule().withInputEntries(new String[] {
                "[25..60]", "-", "Medium" }));
        rules.add(new DecisionRule().withInputEntries(new String[] { ">60",
                "good", "Medium" }));
        rules.add(new DecisionRule().withInputEntries(new String[] { ">60",
                "bad", "High" }));
        model.setRules(rules);
                
        LOGGER.debug("... returning risk rating model");
        return model;
    }
}
