package io.onedecision.engine.decisions.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.ui.DecisionInput;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.decisions.model.ui.DecisionOutput;
import io.onedecision.engine.decisions.model.ui.DecisionRule;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class DecisionUIModelControllerTest implements ExamplesConstants {

    @Autowired
    private DecisionUIModelController controller;

    @Test
    @Ignore
    // TODO currently failing see DecisionExpression.expressions
    public void testInstall() {
        controller.installExamples(TENANT_ID);
    }

    @Test
    public void testLifecycle() {
        DecisionModel model = new DecisionModel();
        model.setName("Test");
        model.getInputs().add(
                new DecisionInput().withName("timeSinceLogin").withLabel(
                        "Time since login"));
        model.getOutputs().add(
                new DecisionOutput().withName("subjectLine").withLabel(
                        "Subject line"));
        model.getRules().add(new DecisionRule()
                .withInputEntries(new String[] { "<P7D", ">=P7D" })
                .withOutputEntries(new String[] { "foo", "bar" }));

        model = controller.createModelForTenant(model, TENANT_ID);
        assertNotNull(model);
        assertNotNull(model.getId());

        List<DecisionModel> models = controller.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        // assertEquals(model, models.get(0));
        assertEquals(model.getId(), models.get(0).getId());
        assertEquals(model.getName(), models.get(0).getName());
        assertEquals(model.getTenantId(), models.get(0).getTenantId());
        assertEquals(model.getInputs().size(), models.get(0)
                .getInputs().size());
        assertEquals(model.getOutputs().size(), models.get(0)
                .getOutputs().size());

        // assertEquals(model.hashCode(), models.get(0).hashCode());
    }

}
