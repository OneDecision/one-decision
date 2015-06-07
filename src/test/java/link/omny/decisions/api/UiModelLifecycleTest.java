package link.omny.decisions.api;

import static org.junit.Assert.assertEquals;

import java.util.List;

import link.omny.decisions.Application;
import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.model.ui.DecisionModel;
import link.omny.decisions.model.ui.examples.ApplicationRiskRatingModel;
import link.omny.decisions.web.DecisionUIModelController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests decision ui model lifecycle (upload/create, list, view, update,
 * delete).
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UiModelLifecycleTest implements ExamplesConstants {

    @Autowired
    protected DecisionUIModelController svc;

    @Test
    public void testLifecycle() {
        DecisionModel model = new ApplicationRiskRatingModel().getModel();

        // Create
        DecisionModel model2 = svc.createModelForTenant(TENANT_ID, model);

        // Retrieve
        List<DecisionModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        // TODO models.get(0).getCreated() will be a java.sql.Timestamp but
        // model.getCreated() is java.util.Date resulting in hashcode difference
        // The date and time represented is the same
        // models.get(0)
        // .setCreated(new Date(models.get(0).getCreated().getTime()));
        model.setCreated(null);
        models.get(0).setCreated(null);
        assertEquals(model2.toString(), models.get(0).toString());
        // assertEquals(model2, models.get(0));

        // Update
        // models.get(0).setName(models.get(0).getName() + " updated");
        svc.updateModelForTenant(TENANT_ID, model.getId(), models.get(0));
        DecisionModel model3 = svc.getModelForTenant(TENANT_ID, model.getId());
        model3.setLastUpdated(null);
        assertEquals(model2.toString(), model3.toString());

        // Delete
        svc.deleteModelForTenant(TENANT_ID, model.getId());
    }
}
