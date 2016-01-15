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
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;

public class AlternateBureauStrategyServiceTest implements ExamplesConstants {

    private static AlternateBureauStrategyServiceExample example;

    private static DecisionEngine de;

    protected TransformUtil transformUtil;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        example = new AlternateBureauStrategyServiceExample();
        de = new InMemoryDecisionEngineImpl();
    }

    @Test
    public void testSerialization() throws Exception {
        de.getRepositoryService().createModelForTenant(example.getDmnModel());
    }

    @Test
    public void testVisualization() throws Exception {
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                example.getDmnModel());
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
                example.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    @Test
    public void testDecisionVisualization() throws Exception {
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                example.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
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
