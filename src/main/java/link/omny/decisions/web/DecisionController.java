package link.omny.decisions.web;

import java.io.IOException;
import java.util.Map;

import link.omny.decisions.api.DecisionsException;
import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.impl.DecisionService;
import link.omny.decisions.model.dmn.Decision;
import link.omny.decisions.model.ui.DecisionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handle decision execution requests as well as discovery of what decisions
 * exist for a given tenant.
 * 
 * @author Tim Stephenson
 * 
 */
@Controller
@RequestMapping("/{tenantId}/decisions")
@RestController
public class DecisionController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionController.class);

    @Autowired
    private DecisionModelFactory decisionModelFactory;

    @Autowired
    private DecisionService decisionService;

    /**
     * Imports DMN files.
     * 
     * @param file
     *            A file posted in a multi-part request
     * @throws IOException
     *             If cannot parse the DMN.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody void handleDmnUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading DMN for: %1$s", tenantId));
        String content = new String(file.getBytes());

        // TODO
        throw new RuntimeException("Not yet implmented");
        // List<Contact> list = objectMapper.readValue(content,
        // new TypeReference<List<Contact>>() {
        // });
        // LOGGER.info(String.format("  found %1$d contacts", list.size()));
        // for (Contact contact : list) {
        // contact.setTenantId(tenantId);
        // }
        //
        // Iterable<Contact> result = contactRepo.save(list);
        // LOGGER.info("  saved.");
        // return result;
    }

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody Iterable<DecisionModel> listForTenant(
            @PathVariable("tenantId") String tenantId)
            throws DecisionsException {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        throw new RuntimeException("This method is not yet implemented");
    }

    /**
     * Executes the decision in the specified definitions bundle.
     * 
     * @param definitionsId
     *            Id for the decision bundle or package. This is the id of the
     *            DMN file's root definitions element.
     * @param decisionId
     *            Id of a particular decision in the bundle.
     * @param JSON
     *            serialised input to the specified decision.
     * @param JSON
     *            serialised output from the specified decision.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{definitionsId}/{decisionId}", headers = "Accept=application/json")
    @ResponseBody
    public final String executeDecision(
            UriComponentsBuilder uriBuilder,
            @PathVariable("tenantId") String tenantId,
            @PathVariable("definitionsId") String definitionsId,
            @PathVariable("decisionId") String decisionId,
            @RequestParam Map<String, String> params) throws IOException,
            DecisionsException {
        LOGGER.info(String.format(
                "handling request to decision: %1$s.%2$s, with params: %3$s",
                definitionsId, decisionId, params));

        Decision d = decisionModelFactory.find(definitionsId, decisionId);
        String jsonOut = decisionService.execute(d, params).get("conclusion");
        LOGGER.info(String.format("decision conclusion: %1$s", jsonOut));

        return jsonOut;
    }
}
