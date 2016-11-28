package io.onedecision.engine.decisions.examples.ch11;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.impl.TransformUtil;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.test.DecisionRule;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class Ch11LoanExampleTest implements ExamplesConstants {

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    private static Ch11LoanExample ch11LoanExample;

    protected TransformUtil transformUtil;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ch11LoanExample = new Ch11LoanExample();
    }

    @Test
    public void testSerialization() throws Exception {
        DmnModel dm = ch11LoanExample.getDmnModel();
        decisionRule.writeDmn(dm.getDefinitions(), dm.getName() + ".dmn");
        assertEquals(0, decisionRule.validate(dm.getDefinitions()).size());
    }

    @Test
    public void testVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(
                ch11LoanExample.getDmnModel());
        String html = ((DecisionModelFactory) decisionRule.getDecisionEngine()
                .getRepositoryService()).getDocumentationForTenant(dmnModel);
        assertNotNull("No visualization created", html);
        for (Decision decision : dmnModel.getDefinitions().getDecisions()) {
            assertTrue(
                    "Cannot find visualisation for " + decision.getId(),
                    html.contains("id=\"" + decision.getId()
                            + "Sect\""));
        }
        decisionRule.writeHtml(html, dmnModel.getDefinitionId() + ".html");
    }

    @Test
    public void testBKMVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(
                ch11LoanExample.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    @Test
    public void testDecisionVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(
                ch11LoanExample.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    private void transformAndAssert(DmnModel dmnModel, String drgElementId)
            throws Exception, IOException {
        String html = ((DecisionModelFactory) decisionRule.getDecisionEngine()
                .getRepositoryService()).getDocumentationForTenant(dmnModel);
        assertNotNull("No visualization created for " + drgElementId,
                html.contains("<section id=\"" + drgElementId + "Sect\""));
        decisionRule.writeHtml(html, drgElementId + ".html");
    }

}
