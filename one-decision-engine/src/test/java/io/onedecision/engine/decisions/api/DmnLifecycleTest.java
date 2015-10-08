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
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.test.MockMultipartFileUtil;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests DMN model lifecycle (upload/create, list, view, update, delete).
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class DmnLifecycleTest implements ExamplesConstants {

    @Autowired
    @Qualifier("springDecisionEngineImpl")
    protected DecisionEngine de;

    /**
     * Minimal test that the Spring configuration annotations are complete and
     * sufficient to start.
     * 
     * For example, this will catch JPA annotation errors, classpath scanning
     * issues etc.
     */
    @Test
    public void contextLoads() {
    }

    @Test
    public void testCrudLifecycle() throws IOException {
        RepositoryService svc = de.getRepositoryService();

        Definitions model = ((DecisionModelFactory) de.getRepositoryService())
                .loadFromClassPath(ARR_DMN_RESOURCE);

        // Create
        DmnModel dmnModel = de.getRepositoryService().createModelForTenant(
                model,
                "A test deployment of " + ARR_DMN_RESOURCE,
                null/* image */, TENANT_ID);

        // Retrieve
        List<DmnModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        assertEquals(dmnModel, models.get(0));
        assertEquals(dmnModel.getDefinitionXml(), models.get(0)
                .getDefinitionXml());

        // Retrieve DMN only
        String definitions = svc.getDmnForTenant(TENANT_ID,
                dmnModel.getDefinitionId());
        assertEquals(dmnModel.getDefinitionXml(), definitions);

        // Update
        models.get(0).setName(models.get(0).getName() + " updated");
        svc.updateModelForTenant(model.getId(), models.get(0), TENANT_ID);
        DmnModel model2 = svc.getModelForTenant(dmnModel.getDefinitionId(),
                TENANT_ID);
        assertEquals(dmnModel.getDefinitionXml(), model2.getDefinitionXml());

        // Delete
        svc.deleteModelForTenant(dmnModel.getId(), TENANT_ID);
    }

    @Test
    public void testUpload() throws IOException {
        DecisionDmnModelController svc = (DecisionDmnModelController) de
                .getRepositoryService();

        // Definitions referenceModel = fac.loadFromClassPath(ARR_DMN_RESOURCE);
        File baseDir = new File("target" + File.separator + "test-classes");
        File dmnToUpload = new File(baseDir, ARR_DMN_RESOURCE);
        assertTrue("Cannot find DMN file to use as test input",
                dmnToUpload.exists());

        // Create
        DmnModel dmnModel = svc.handleFileUpload(TENANT_ID, null,
                MockMultipartFileUtil.newInstance(ARR_DMN_RESOURCE));

        // Retrieve
        List<DmnModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        assertEquals(dmnModel.getDefinitionXml(), models.get(0)
                .getDefinitionXml());

        // Delete
        svc.deleteModelForTenant(dmnModel.getId(), TENANT_ID);
    }
}
