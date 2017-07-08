package io.onedecision.engine.test;

import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;
import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class for use in writing unit tests to make them more succinct.
 *
 * @deprecated Use io.onedecision.engine.test.DecisionRule instead.
 */
public class TestHelper {

    private static SchemaValidator schemaValidator;

    private static DecisionEngine de;

    protected static ObjectMapper mapper;

    public static SchemaValidator getSchemaValidator() {
        if (schemaValidator == null) {
            schemaValidator = new SchemaValidator();
        }
        return schemaValidator;
    }

    public static void setSchemaValidator(SchemaValidator schemaValidator) {
        TestHelper.schemaValidator = schemaValidator;
    }

    public static DecisionEngine getDecisionEngine() {
        if (de == null) {
            de = new InMemoryDecisionEngineImpl();
        }
        return de;
    }

    public static void setDecisionEngine(DecisionEngine de) {
        TestHelper.de = de;
    }

    public static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        TestHelper.mapper = mapper;
    }

    /**
     * @deprecated Since 1.2 focus on DMN models as there are now plenty of
     *             modeling tools.
     */
    public static DecisionModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DecisionModel jsonModel = getMapper().readValue(
                TestHelper.class.getResourceAsStream(resource),
                DecisionModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }
}
