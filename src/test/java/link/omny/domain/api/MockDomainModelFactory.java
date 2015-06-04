package link.omny.domain.api;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import link.omny.domain.api.DomainModelFactory;
import link.omny.domain.model.DomainModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockDomainModelFactory implements DomainModelFactory {
    protected static ObjectMapper mapper;

    protected Map<String, DomainModel> domains;

    public MockDomainModelFactory() {
        mapper = new ObjectMapper();
        domains = new HashMap<String, DomainModel>();
    }

    public MockDomainModelFactory(String key, String jsonResource)
            throws JsonParseException, JsonMappingException, IOException {
        this();
        domains.put(key, getJsonModel(jsonResource));
    }

    @Override
    public DomainModel fetchDomain(String domainModelUri) {
        if (domains.containsKey(domainModelUri)) {
            return domains.get(domainModelUri);
        } else {
            throw new IllegalStateException(String.format(
                    "No domain model registered for URI: %1$s", domainModelUri));
        }
    }

    private DomainModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DomainModel jsonModel = mapper.readValue(getClass()
                .getResourceAsStream(resource), DomainModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }
}
