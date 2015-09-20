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
package io.onedecision.engine.decisions.web;

import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.api.DecisionModelFactory;
import io.onedecision.engine.decisions.api.DecisionNotFoundException;
import io.onedecision.engine.decisions.api.DecisionService;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.repositories.DecisionDmnModelRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handle decision execution requests as well as discovery of what decisions
 * exist for a given tenant.
 * 
 * @author Tim Stephenson
 * 
 */
@Controller
@RequestMapping("/{tenantId}/onedecision")
public class DecisionController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionController.class);

    @Autowired
	protected DecisionDmnModelRepository repo;

    @Autowired
    protected DecisionModelFactory decisionModelFactory;

    @Autowired
    protected DecisionService decisionService;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Executes the decision in the specified definitions bundle.
     * 
     * @param definitionId
     *            Id for the decision bundle or package. This is the id of the
     *            DMN file's root definitions element.
     * @param decisionId
     *            Id of a particular decision in the bundle.
     * @param params
     *            <code>Map</code> of parameters expected as input to the
     *            specified decision. Values are expected to be JSON serialized.
     * @return JSON serialised output from the specified decision.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{definitionId}/{decisionId}", headers = "Accept=application/json")
    @ResponseBody
    public final String executeDecision(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("definitionId") String definitionId,
            @PathVariable("decisionId") String decisionId,
            @RequestParam Map<String, Object> params) throws IOException,
            DecisionException {
        LOGGER.info(String.format(
                "handling request to decision: %1$s.%2$s, with params: %3$s",
                definitionId, decisionId, params));

		DmnModel dmnModel = repo.findByDefinitionId(tenantId, definitionId);
        if (dmnModel == null) {
            throw new DecisionNotFoundException(tenantId, definitionId,
                    decisionId);
        }
		Definitions definitions = decisionModelFactory.load(dmnModel
				.getDefinitionXml());
        Map<String, Object> results = decisionService.execute(definitions,
                decisionId, params);

        LOGGER.info(String.format("decision conclusion: %1$s", results));
        return toJson(results);
    }

    private String toJson(Map<String, Object> results) throws IOException {
        StringBuffer sb = new StringBuffer("{");
        for (Entry<String, Object> entry : results.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":");
            Object val = entry.getValue();
            if (val instanceof String && val.equals("{}")) {
                sb.append(val);
            } else {
                val = objectMapper.writeValueAsString(val);
                if (val instanceof String && ((String) val).startsWith("\"{")) {
                    val = ((String) val).substring(1,
                            ((String) val).length() - 1);
                }
                sb.append(((String) val).replaceAll("\\\\", ""));
            }
            sb.append(",");
        }
        if (sb.lastIndexOf(",") != -1) {
            sb.deleteCharAt(sb.lastIndexOf(",")).append("}");
        }
        return sb.toString();
    }
}
