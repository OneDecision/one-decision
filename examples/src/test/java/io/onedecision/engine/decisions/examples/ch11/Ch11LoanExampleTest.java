package io.onedecision.engine.decisions.examples.ch11;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.impl.TransformUtil;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class Ch11LoanExampleTest implements ExamplesConstants {

    private static Ch11LoanExample ch11LoanExample;

    private static DecisionEngine de;

    protected TransformUtil transformUtil;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ch11LoanExample = new Ch11LoanExample();
        de = new InMemoryDecisionEngineImpl();
    }

    @Test
    public void testSerialization() throws Exception {
        de.getRepositoryService().createModelForTenant(
                ch11LoanExample.getDmnModel());
    }

    @Test
    public void testVisualization() throws Exception {
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                ch11LoanExample.getDmnModel());
        String html = getTransformUtil().transform(dmnModel.getDefinitionXml());
        assertNotNull("No visualization created", html);
        for (Decision decision : dmnModel.getDefinitions().getDecisions()) {
            assertTrue(
                    "Cannot find visualisation for " + decision.getId(),
                    html.contains("<section id=\"" + decision.getId()
                            + "Sect\""));
        }
        write(html, dmnModel.getDefinitionId() + ".html");
    }

    @Test
    public void testBKMVisualization() throws Exception {
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                ch11LoanExample.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    @Test
    public void testDecisionVisualization() throws Exception {
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                ch11LoanExample.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    @Test
    public void testJsonDeserialization() throws Exception {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(
                    "/decisions/examples/Ch11LoanExample.json");
            ObjectMapper mapper = new ObjectMapper();
            // we'll be reading instances of MyBean
            ObjectReader reader = mapper.readerFor(DmnModel.class);
            // and then do other configuration, if any, and read:
            DmnModel result = reader.readValue(is);
            assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            // fail("unable to find test resource");
        }
        // DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
        // ch11LoanExample.getDmnModel());
        // transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    private void transformAndAssert(DmnModel dmnModel, String drgElementId)
            throws Exception, IOException {
        String html = getTransformUtil().transform(dmnModel.getDefinitionXml(),
                Collections.singletonMap("drgElementId", drgElementId));
        assertNotNull("No visualization created for " + drgElementId,
                html.contains("<section id=\"" + drgElementId + "Sect\""));
        write(html, drgElementId + ".html");
    }

    protected void write(String html, String fileName) throws IOException {
        File file = new File("target", fileName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            if (!html.contains("<html>")) {
                writer.write("<html><body>");
            }
            writer.write(html);
            if (!html.contains("<html>")) {
                writer.write("</body><html>");
            }
        } finally {
            writer.close();
        }
    }

    protected TransformUtil getTransformUtil() throws Exception {
        if (transformUtil == null) {
            transformUtil = new TransformUtil();
            transformUtil.setXsltResources("/static/xslt/dmn2html.xslt");
        }
        return transformUtil;
    }

}
