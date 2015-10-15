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

        List<DecisionCondition> conditions = new ArrayList<DecisionCondition>();
        conditions.add(new DecisionCondition("Since last email", new String[] {
                "< 7d", ">= 7d", "", "", "", "", "", "", "", "" }));
        conditions.add(new DecisionCondition("Since registration",
                new String[] { "", "", ">= 2w", ">= 6w", ">= 10 weeks",
                        ">= 52 weeks", "", "" }));
        conditions.add(new DecisionCondition("Since login",
                new String[] { "", "", "", "", "", "", ">= 4 weeks",
                        ">= 12 weeks", ">= 24 weeks" }));
        conditions.add(new DecisionCondition("Not yet sent",
                new String[] { "discover", "intro-services",
                        "business-sale-ideas", "anniversary whats-on",
                        "is-there-progress", "need-a-hand", "" }));
        // conditions.add(new DecisionCondition("Otherwise", new String[] { "",
        // "", "", "", "", "", "", "", "", "", "", "", "", "", "true" }));
        model.setConditions(conditions);

        List<DecisionConclusion> conclusions = new ArrayList<DecisionConclusion>();
        conclusions
                .add(new DecisionConclusion("Template to use", new String[] {
                        "discover-firmgains", "intro-services",
                        "business-sale-ideas", "anniversary", "whats-on",
                        "is-there-progress", "need-a-hand" }));
        conclusions.add(new DecisionConclusion("Subject Line", new String[] {
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
