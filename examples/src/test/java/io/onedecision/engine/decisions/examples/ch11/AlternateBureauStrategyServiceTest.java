package io.onedecision.engine.decisions.examples.ch11;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.test.DecisionRule;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class AlternateBureauStrategyServiceTest implements ExamplesConstants {

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    private static AlternateBureauStrategyServiceExample example;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        example = new AlternateBureauStrategyServiceExample();
    }

    @Test
    public void testSerialization() throws Exception {
        DmnModel dm = example.getDmnModel();
        decisionRule.writeDmn(dm.getDefinitions(), dm.getName());
        decisionRule.validate(dm.getDefinitions());
    }

    @Test
    public void testVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(example.getDmnModel());
        String html = ((DecisionModelFactory) decisionRule.getDecisionEngine()
                .getRepositoryService()).getDocumentationForTenant(dmnModel);
        assertNotNull("No visualization created", html);
        decisionRule.writeHtml(html, dmnModel.getDefinitionId() + ".html");

        for (Decision decision : dmnModel.getDefinitions().getDecisions()) {
            assertTrue(
                    "Cannot find visualisation for " + decision.getId(),
                    html.contains("id=\"" + decision.getId()
                            + "Sect\""));
        }
    }

    @Test
    public void testBKMVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(example.getDmnModel());
        transformAndAssert(dmnModel, "applicationRiskScoreModel_bkm");
    }

    @Test
    public void testDecisionVisualization() throws Exception {
        DmnModel dmnModel = decisionRule.getDecisionEngine()
                .getRepositoryService()
                .createModelForTenant(example.getDmnModel());
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
