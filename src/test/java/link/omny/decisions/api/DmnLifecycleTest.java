package link.omny.decisions.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import link.omny.decisions.Application;
import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.model.dmn.Definitions;
import link.omny.decisions.model.dmn.DmnModel;
import link.omny.decisions.test.MockMultipartFileUtil;
import link.omny.decisions.web.DecisionDmnModelController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests DMN model lifecycle (upload/create, list, view, update, delete).
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DmnLifecycleTest implements ExamplesConstants {

    @Autowired
    protected DecisionModelFactory fac;

    @Autowired
    protected DecisionDmnModelController svc;

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
        Definitions model = fac.loadFromClassPath(ARR_DMN_RESOURCE);

        // Create
        DmnModel dmnModel = svc.createModelForTenant(TENANT_ID,
                ARR_DMN_RESOURCE.substring(ARR_DMN_RESOURCE.lastIndexOf('/')),
                "A test deployment of " + ARR_DMN_RESOURCE, model);

        // Retrieve
        List<DmnModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        assertEquals(dmnModel.getDefinitionXml(), models.get(0)
                .getDefinitionXml());

        // Update
        models.get(0).setName(models.get(0).getName() + " updated");
        svc.updateModelForTenant(TENANT_ID, model.getId(), models.get(0));
        DmnModel model2 = svc.getModelForTenant(TENANT_ID, model.getId());
        assertEquals(dmnModel.getDefinitionXml(), model2.getDefinitionXml());

        // Delete
        svc.deleteModelForTenant(TENANT_ID, dmnModel.getId());
    }

    @Test
    public void testUpload() throws IOException {
        // Definitions referenceModel = fac.loadFromClassPath(ARR_DMN_RESOURCE);
        File baseDir = new File("target" + File.separator + "test-classes");
        File dmnToUpload = new File(baseDir, ARR_DMN_RESOURCE);
        assertTrue("Cannot find DMN file to use as test input",
                dmnToUpload.exists());
        Path path = Paths.get(dmnToUpload.getAbsolutePath());
        byte[] content = Files.readAllBytes(path);

        // Create
        DmnModel dmnModel = svc.handleFileUpload(TENANT_ID, null,
                MockMultipartFileUtil.newInstance(ARR_DMN_RESOURCE));

        // Retrieve
        List<DmnModel> models = svc.listForTenant(TENANT_ID);
        assertEquals(1, models.size());
        assertEquals(dmnModel.getDefinitionXml(), models.get(0)
                .getDefinitionXml());

        // Delete
        svc.deleteModelForTenant(TENANT_ID, dmnModel.getId());
    }
}
