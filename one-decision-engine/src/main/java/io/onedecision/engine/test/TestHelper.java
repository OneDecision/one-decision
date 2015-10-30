package io.onedecision.engine.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;
import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class for use in writing unit tests to make them more succinct.
 *
 * @author Tim Stephenson
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

    public static DecisionModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DecisionModel jsonModel = getMapper().readValue(
                TestHelper.class.getResourceAsStream(resource),
                DecisionModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }

    public static void assertSerializationProduced(Definitions dm)
            throws IOException, FileNotFoundException {
        Assert.assertNotNull("Definitions produced must not be null", dm);

        File dmnFile = new File("target", dm.getId() + ".dmn");
        FileWriter out = new FileWriter(dmnFile);
        try {
            getDecisionEngine().getRepositoryService().write(dm, out);
        } finally {
            out.close();
        }
        System.out.println("Wrote dmn to: " + dmnFile);
        assertTrue(dmnFile.exists());

        InputStream fis = null;
        try {
            fis = new FileInputStream(dmnFile);
            DmnValidationErrors errors = new DmnValidationErrors();
            getSchemaValidator().validate(fis, errors);
            assertTrue(!errors.hasErrors());
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                ;
            }
        }
    }
}
