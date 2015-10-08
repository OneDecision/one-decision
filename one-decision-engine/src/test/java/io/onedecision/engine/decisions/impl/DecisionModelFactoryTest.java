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
package io.onedecision.engine.decisions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnElementReference;
import io.onedecision.engine.decisions.model.dmn.DtInput;
import io.onedecision.engine.decisions.model.dmn.DtOutput;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class DecisionModelFactoryTest implements ExamplesConstants {

    private static DecisionEngine de;

    private static Validator validator;

    @BeforeClass
    public static void setUp() throws Exception {
        de = new InMemoryDecisionEngineImpl();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testReadAndValidateApplicantRiskRating() throws IOException {
        DecisionModelFactory fact = (DecisionModelFactory) de
                .getRepositoryService();
        Definitions dm = fact.loadFromClassPath(ARR_DMN_RESOURCE);

        assertNotNull("Definitions element was null", dm);
        assertEquals(2, dm.getItemDefinitions().size());
        assertEquals(1, dm.getDecisions().size());
        assertEquals(2, dm.getInformationItems().size());
        assertEquals("applicant", dm.getInformationItems().get(0).getId());
        assertEquals("conclusion", dm.getInformationItems().get(1).getId());
        // TODO What about this:
        // <LiteralExpression expressionLanguage="http://tempuri.org"
        // id="idvalue2" name="conclusion">...

        for (Decision d : dm.getDecisions()) {
            assertEquals(ExamplesConstants.ARR_DECISION_ID, d.getId());

            DecisionTable dt = d.getDecisionTable();
            assertNotNull(dt);
            assertEquals("dt0", dt.getId());
            if (dt != null) {
                List<DtInput> inputs = dt.getInputs();
                assertEquals(3, inputs.size());
                for (int i = 0; i < 3; i++) {
                    DtInput input = inputs.get(i);
                    switch (i) {
                    case 0:
                        Expression inExpr = input.getInputExpression();
                        assertNotNull(inExpr);
                        assertEquals("dt0_c0_ie", inExpr.getId());
                        // TODO dmn11
                        // assertEquals(1, inExpr.getInputVariable().size());
                        // assertEquals("applicant", ((InformationItem) inExpr
                        // .getInputVariable().get(0).getValue()).getId());
                        // assertEquals("applicant",
                        // inExpr.getOnlyInputVariable()
                        // .getId());
                        assertEquals(3, input.getInputValues().size());
                        for (int j = 0; j < 3; j++) {
                            Expression inEntry = input.getInputValues().get(j);
                            switch (j) {
                            case 0:
                                assertTrue(inEntry instanceof LiteralExpression);
                                LiteralExpression le = (LiteralExpression) inEntry;
                                assertEquals("age > 60", le.getText());
                                break;
                            }
                        }
                        break;
                    case 1:
                        inExpr = input.getInputExpression();
                        assertNotNull(inExpr);
                        assertEquals("dt0_c1_ie", inExpr.getId());
                     // TODO dmn11
//                        assertEquals(1, inExpr.getInputVariable().size());
//                        assertEquals("applicant", ((InformationItem) inExpr
//                                .getInputVariable().get(0).getValue()).getId());
//                        assertEquals("applicant", inExpr.getOnlyInputVariable()
//                                .getId());
                        break;
                    }
                }
                
                List<DtOutput> outputs = dt.getOutputs();
                assertEquals(3, outputs.size());
                for (int i = 0; i < 3; i++) {
                    DtOutput output = outputs.get(i);
                    switch (i) {
                    case 0:
                        DmnElementReference outDef = output
                                .getOutputDefinition();
                        assertNotNull(outDef);
                        assertEquals("#conclusion", outDef.getHref());
                        assertEquals(3, output.getOutputValues().size());

                        break;
                    }
                    for (LiteralExpression outputEntry : output
                            .getOutputValues()) {
                        System.out.println("in: " + outputEntry.getId());
                    }
                    List<DecisionRule> rules = dt.getRules();
                    for (DecisionRule rule : rules) {
                        System.out.println("rule: " + rule);
                        System.out.println("rule condition: "
                                + rule.getInputEntry());
                        System.out.println("rule conclusion: "
                                + rule.getOutputEntry());
                    }
                }
            }
        }

        Set<ConstraintViolation<Definitions>> constraintViolations = validator
                .validate(dm);
        for (ConstraintViolation<Definitions> violation : constraintViolations) {
            System.out.println("  "
                    + violation.getLeafBean().getClass().getName() + "."
                    + violation.getPropertyPath() + " "
                    + violation.getMessage());
        }
        assertEquals("Decision Table is not valid", 0,
                constraintViolations.size());

        File f = new File("target", dm.getName() + ".dmn");
        FileWriter writer = null;
        try {
            writer = new FileWriter(f);
            fact.write(dm, writer);
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
        assertTrue(f.exists());
        System.out.println("wrote dmn to :" + f.getAbsolutePath());
    }
}
