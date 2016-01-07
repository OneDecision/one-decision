package io.onedecision.engine.domain.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;
import io.onedecision.engine.domain.model.EntityField;

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
public class DomainModelControllerTest implements ExamplesConstants {

    @Autowired
    private DomainController controller;

    @Test
    public void testLifecycle() {
        DomainModel model = new DomainModel().withName("Test");
        DomainEntity entity = new DomainEntity()
                .withName("Contact")
                .withField(
                        new EntityField()
                                .withName("firstName")
                                .withLabel("First Name")
                                .withType("text"));
        model.getEntities().add(entity);

        // CREATE
        model = controller.updateModelForTenant(TENANT_ID, model);
        assertNotNull(model);
        assertNotNull(model.getId());
        assertEquals(1, model.getEntities().size());
        assertEquals(Long.valueOf(1l), model.getRevision());

        // RETRIEVE
        DomainModel modelRetrieved = controller.getModelForTenant(TENANT_ID);
        assertEquals(model.getId(), modelRetrieved.getId());
        assertEquals(model.getName(), modelRetrieved.getName());
        assertEquals(model.getTenantId(), modelRetrieved.getTenantId());
        assertEquals(1, modelRetrieved.getEntities().size());
        assertEquals(Long.valueOf(1l), modelRetrieved.getRevision());

        // UPDATE entire model
        modelRetrieved.withEntity(new DomainEntity().withName("Account"));
        DomainModel updatedModel = controller.updateModelForTenant(TENANT_ID,
                modelRetrieved);
        assertEquals(2, updatedModel.getEntities().size());
        assertEquals(Long.valueOf(2l), updatedModel.getRevision());

        // UPDATE single entity
        DomainEntity contactEntity = updatedModel.getEntities().get(0);
        contactEntity.withField(new EntityField().withName("lastName")
                .withLabel("Last Name").withType("text"));
        controller.updateEntityForTenant(TENANT_ID, "Contact", contactEntity);

        DomainModel model3 = controller.getModelForTenant(TENANT_ID);
        assertEquals(2, model3.getEntities().size());
        assertEquals(2, model3.getEntities().get(0).getFields().size());
        assertEquals(Long.valueOf(3l), model3.getRevision());

        // DELETE
        controller.deleteModelForTenant(TENANT_ID);
    }

}
