package io.onedecision.engine.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;

import org.junit.BeforeClass;
import org.junit.Test;

public class ClasspathDomainModelFactoryTest {

    private static final String CUST_MGMT_DOMAIN_JSON = "/domains/cust-mgmt.json";
    private static final String CUST_MGMT_URI = "http://onedecision.io/domains/cust-mgmt";
    private static DomainModelFactory domainModelFactory;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        domainModelFactory = new ClasspathDomainModelFactory();
        ((ClasspathDomainModelFactory) domainModelFactory).defaultDomainUri = CUST_MGMT_URI;
        ((ClasspathDomainModelFactory) domainModelFactory).defaultDomainResource = CUST_MGMT_DOMAIN_JSON;
    }

    @Test
    public void testReadCustMgmtDomain() {
        try {
            DomainModel domain = domainModelFactory.fetchDomain(CUST_MGMT_URI);
            assertNotNull(domain);
            assertEquals(2, domain.getEntities().size());

            DomainEntity entity = domain.getEntities().get(0);
            switch (entity.getName()) {
            case "Contact":
                assertContactEntityDefinition(entity);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    private void assertContactEntityDefinition(DomainEntity entity) {
        assertEquals(5, entity.getFields().size());
    }

}
