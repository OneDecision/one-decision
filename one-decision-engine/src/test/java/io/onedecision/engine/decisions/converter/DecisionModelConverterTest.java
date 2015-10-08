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
package io.onedecision.engine.decisions.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.domain.api.test.MockDomainModelFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionModelConverterTest implements ExamplesConstants {

    protected static ObjectMapper mapper;
    protected static DecisionModelConverter converter;

    @BeforeClass
    public static void setUpOnce() {
        mapper = new ObjectMapper();
        new File("target/decisions").mkdirs();

        converter = new DecisionModelConverter();
    }

    @Test
    @Ignore
    public void testConvertSingleDecisionTable() throws JsonParseException,
            JsonMappingException, IOException, DecisionException {
        DecisionModel jsonModel = getJsonModel(ARR_JSON_RESOURCE);
        converter.setDomainModelFactory(new MockDomainModelFactory(
				"http://onedecision.io/health", "/domains/health.json"));

        Definitions dmnModel = converter.convert(jsonModel);
        Decision d = dmnModel.getDecision(ARR_DECISION_ID);
		assertNotNull(d);

		DecisionTable dt = d.getDecisionTable();
		assertNotNull(dt);
		assertEquals(3, dt.getInputs().size());

		assertEquals(5, dt.getRules().size());
        for (DecisionRule rule : dt.getRules()) {
            assertEquals(2, rule.getConditions().size());
            assertEquals(1, rule.getConclusions().size());
        }

        File dmnFile = new File("target", ARR_DEFINITION_ID + ".dmn");
        FileWriter writer = null;
        try {
            writer = new FileWriter(dmnFile);
            new DecisionModelFactory().write(dmnModel, writer);
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
        assertTrue(dmnFile.exists());

        // TODO validate the result using all registered validators
        // http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html

		SchemaValidator schemaValidator = new SchemaValidator();
		DmnValidationErrors errors = new DmnValidationErrors();
		schemaValidator.validate(new FileInputStream(dmnFile), errors);
		assertEquals(0, errors.getErrorCount());
    }

    private DecisionModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DecisionModel jsonModel = mapper.readValue(getClass()
                .getResourceAsStream(resource), DecisionModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }
}
