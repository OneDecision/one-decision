package link.omny.decisions.converter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import link.omny.decisions.api.DecisionsException;
import link.omny.decisions.converter.DecisionModelConverter;
import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.model.dmn.Definitions;
import link.omny.decisions.model.ui.DecisionModel;
import link.omny.domain.api.MockDomainModelFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionModelConverterTest {

    protected static final String APPLICANT_RISK_RATING_DECISION = "/decisions/applicant-risk-rating";
    protected static final String FG_EMAIL_DECISION = "/decisions/firmgains-email-decision";
    protected static final String EMAIL_DECISION = "/decisions/email-decision";
    protected static ObjectMapper mapper;
    protected static DecisionModelConverter converter;

    @BeforeClass
    public static void setUpOnce() {
        mapper = new ObjectMapper();
        new File("target/decisions").mkdirs();
        
        converter = new DecisionModelConverter();
    }

    @Test
    public void testConvertSingleDecisionTable() throws JsonParseException,
            JsonMappingException, IOException, DecisionsException {
        DecisionModel jsonModel = getJsonModel(APPLICANT_RISK_RATING_DECISION
                + ".json");
        converter.setDomainModelFactory(new MockDomainModelFactory(
                "http://omny.link/health", "/domains/health.json"));

        Definitions dmnModel = converter.convert(jsonModel);

        File dmnFile = new File("target", APPLICANT_RISK_RATING_DECISION
                + ".dmn");
        // dmnModel.write(new FileWriter(dmnFile));
        new DecisionModelFactory().write("application/xml", dmnModel,
                dmnFile);
        assertTrue(dmnFile.exists());

        // TODO validate the result (at least schema valid)
        // http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html
    }

    private DecisionModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DecisionModel jsonModel = mapper.readValue(getClass()
                .getResourceAsStream(resource), DecisionModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }
}
