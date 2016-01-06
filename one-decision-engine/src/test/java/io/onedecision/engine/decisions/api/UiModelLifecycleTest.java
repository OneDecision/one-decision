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
package io.onedecision.engine.decisions.api;

import static org.junit.Assert.assertEquals;
import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.decisions.model.ui.examples.ApplicationRiskRatingModel;

import java.util.List;

import org.junit.Ignore;
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
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class UiModelLifecycleTest implements ExamplesConstants {

    @Autowired
    protected ModelingService svc;

    @Test
    @Ignore
    public void testLifecycle() {
        DecisionModel model = new ApplicationRiskRatingModel().getModel();

        // Create
        DecisionModel model2 = svc.createModelForTenant(model, TENANT_ID);

        // Retrieve
        List<DecisionModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        assertEquals(model2.getId(), models.get(0).getId());
        // assertEquals(model2, models.get(0));

        // Update
        // models.get(0).setName(models.get(0).getName() + " updated");
        svc.updateModelForTenant(model.getId(), models.get(0), TENANT_ID);
        DecisionModel model3 = svc.getModelForTenant(model.getId(), TENANT_ID);
        model3.setLastUpdated(null);
        assertEquals(model2.getId(), model3.getId());

        // Delete
        svc.deleteModelForTenant(model.getId(), TENANT_ID);
    }
}
