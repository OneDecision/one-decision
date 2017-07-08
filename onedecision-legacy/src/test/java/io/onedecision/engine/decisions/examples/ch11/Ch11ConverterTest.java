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
package io.onedecision.engine.decisions.examples.ch11;

import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.converter.DecisionModelConverter;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.test.DecisionRule;
import io.onedecision.engine.test.TestHelper;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Test conversion of Strategy model from JSON form to DMN.
 *
 *
 * @author Tim Stephenson
 */
public class Ch11ConverterTest {
    
    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    static final String CH11_FIG70_DECISION_ID = "_70_d";
    static final String CH11_FIG70_JSON_RESOURCE = "/decisions/examples/Fig70Strategy.json";

    static final String CH11_FIG72_DECISION_ID = "_72_d";
    static final String CH11_FIG72_JSON_RESOURCE = "/decisions/examples/Fig72BureauCallType.json";

    static final String CH11_FIG74_DECISION_ID = "_74_d";
    static final String CH11_FIG74_JSON_RESOURCE = "/decisions/examples/Fig74EligibilityRules.json";

    static final String CH11_FIG76_DECISION_ID = "_76_d";
    static final String CH11_FIG76_JSON_RESOURCE = "/decisions/examples/Fig76PreBureauRiskCategoryTable.json";

    protected static DecisionModelConverter converter;

    @BeforeClass
    public static void setUpOnce() {
        new File("target/decisions").mkdirs();

        converter = new DecisionModelConverter();
    }

    @Test
    public void testConvertStrategyDecisionTable() throws JsonParseException,
            JsonMappingException, IOException, DecisionException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG70_JSON_RESOURCE);

        Definitions dmnModel = converter.convert(jsonModel);
        Decision d = dmnModel.getDecision(CH11_FIG70_DECISION_ID);
        assertNotNull(d);
        //
        // DecisionTable dt = d.getDecisionTable();
        // assertNotNull(dt);
        // assertEquals(3, dt.getInputs().size());
        //
        // assertEquals(5, dt.getRules().size());
        // for (DecisionRule rule : dt.getRules()) {
        // assertEquals(2, rule.getInputEntry().size());
        // assertEquals(1, rule.getOutputEntry().size());
        // }

        decisionRule.writeDmn(dmnModel, dmnModel.getName());
        decisionRule.validate(dmnModel);
    }

    @Test
    public void testConvertBureauCallTypeDecisionTable()
            throws JsonParseException,
            JsonMappingException, IOException, DecisionException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG72_JSON_RESOURCE);

        Definitions dmnModel = converter.convert(jsonModel);
        Decision d = dmnModel.getDecision(CH11_FIG72_DECISION_ID);
        assertNotNull(d);

        // TODO validate the result using all regisdmnModelnModel);
        decisionRule.writeDmn(dmnModel, dmnModel.getName());
        decisionRule.validate(dmnModel);
    }

    @Test
    public void testConvertEligibilityRulesDecisionTable()
            throws JsonParseException, JsonMappingException, IOException,
            DecisionException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG74_JSON_RESOURCE);

        Definitions dmnModel = converter.convert(jsonModel);
        Decision d = dmnModel.getDecision(CH11_FIG74_DECISION_ID);
        assertNotNull(d);

        decisionRule.writeDmn(dmnModel, dmnModel.getName());
        decisionRule.validate(dmnModel);
    }

    @Test
    public void testConvertPreBureauRiskCategoryDecisionTable()
            throws JsonParseException, JsonMappingException, IOException,
            DecisionException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG76_JSON_RESOURCE);

        Definitions dmnModel = converter.convert(jsonModel);
        Decision d = dmnModel.getDecision(CH11_FIG76_DECISION_ID);
        assertNotNull(d);

        decisionRule.writeDmn(dmnModel, dmnModel.getName());
        decisionRule.validate(dmnModel);
    }

}
