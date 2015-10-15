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
package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.api.RuntimeService;
import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.DtInput;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.Import;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionService implements DecisionConstants, RuntimeService {

	protected static final Logger LOGGER = LoggerFactory.getLogger(DecisionService.class);

    public DecisionEngine de;

	protected List<DelExpression> compilers;

    private Map<String, String> cache = new HashMap<String, String>();
    private ScriptEngine jsEng;

    private static final List<Character> OPERATORS = Arrays
            .asList(new Character[] { '<', '=', '!', '>' });;

    private static List<String> newArrayList(String... objects) {
        List<String> list = new ArrayList<String>();
        for (String s : objects) {
            list.add(s);
        }
        return Collections.unmodifiableList(list);
    }

    public DecisionService() {
        ScriptEngineManager sem = new ScriptEngineManager();
        jsEng = sem.getEngineByName("JavaScript");
    }

    public void setDecisionEngine(DecisionEngine de) {
        this.de = de;
    }

	public List<DelExpression> getDelExpressions() {
		if (compilers == null) {
			compilers = new ArrayList<DelExpression>();
		}
		return compilers;
	}

	public void setDelExpressions(List<DelExpression> compilers) {
		this.compilers = compilers;
	}

    @Override
    public Map<String, Object> executeDecision(String definitionId,
            String decisionId, Map<String, Object> params, String tenantId)
            throws DecisionException,
            DecisionException {
        DmnModel model = de.getRepositoryService().getModelForTenant(
                definitionId, tenantId);
        return execute(model.getDefinitions(), decisionId, params);
    }

    // TODO Should we simply return Object as only single value possible?
    public Map<String, Object> execute(Definitions dm, String decisionId,
            Map<String, Object> vars) throws DecisionException {
        String script = getScript(dm, decisionId);
        Decision decision = dm.getDecision(decisionId);
        Map<String, Object> results = execute(decision,
                script, vars);

        return Collections.singletonMap(decision.getInformationItem().getId(),
                results.get(decision.getInformationItem().getId()));
    }

    protected Map<String, Object> execute(Decision d,
            String script, Map<String, Object> params) throws DecisionException {
        for (Entry<String, Object> o : params.entrySet()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("JSON input in Java: " + o);
			}
            jsEng.put(o.getKey(), o.getValue());
        }

        try {
            Object r = jsEng.eval(script);
            LOGGER.debug("  response: " + r);
        } catch (ScriptException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new DecisionException("Unable to evaluate decision", ex);
        }

        // TODO rather than placing return type into params map, could return a
        // single object?
        for (Entry<String, Object> o2 : jsEng.getBindings(
                ScriptContext.ENGINE_SCOPE).entrySet()) {
            if (o2.getKey().equals(d.getInformationItem().getId())) {
                params.put(o2.getKey(), o2.getValue());
            }
        }

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("vars returned: " + params);
		}
        return params;
    }

    public String getScript(Definitions dm, String decisionId) {
        StringBuilder sb = new StringBuilder();
        
        for (Import import_ : dm.getImports()) {
            if (EXPR_URI_JS.equals(import_.getImportType())) {
                sb.append("load('" + import_.getLocationURI() + "');\n");
            }
        }
        
        // init vars
        // root objects must be listed as InputData at the Definitions level
        // but we'll leave that as a task for the validator for now.
        Decision d = dm.getDecision(decisionId); 
        for (DtInput input : d.getDecisionTable().getInputs()) {
            String rootObject = getRootObject(input.getInputExpression()
                    .getText());
            sb.append("if (" + rootObject + "==undefined) var " + rootObject
                    + " = {};\n");
            sb.append("if (typeof " + rootObject + "=='string' && "
                    + rootObject + ".charAt(0)=='{') " + rootObject
                    + " = JSON.parse(" + rootObject + ");\n");
        }

        sb.append("var " + getRootObject(d.getInformationItem().getId())
                    + " = {};\n");

        return getScript(sb, d);
    }

    protected String getRootObject(String text) {
        if (text.indexOf('.') != -1) {
            return text.substring(0, text.indexOf('.'));
        }
        return text;
    }

    protected String getScript(StringBuilder sb, Decision d) {
        DecisionTable dt = d.getDecisionTable();
        if (cache.containsKey(dt.getId())) {
            return cache.get(dt.getId());
        }

        // Rhino _and_ Nashorn compatible way to enable access to println
        sb.append("var System = java.lang.System;\n");

        sb.append(createFunctionName(dt.getId())).append("();\n\n");
        
        sb.append("function ").append(createFunctionName(dt.getId()))
                .append("() {\n");
        int ruleIdx = 0;
        for (DecisionRule rule : dt.getRules()) {
            ruleIdx++;
            List<LiteralExpression> conditions = rule.getInputEntry();
            for (int i = 0; i < conditions.size(); i++) {
                LiteralExpression inputExpression = dt.getInputs().get(i)
                        .getInputExpression();
                if (i == 0) {
                    sb.append("if (");
                } else {
                    sb.append(" && ");
                }
                Expression ex = conditions.get(i);
                sb.append(inputExpression.getText());
                if (ex instanceof LiteralExpression) {
                    LiteralExpression le = (LiteralExpression) ex;
                    if (isLiteral(le.getText())) {
                        sb.append(" == ");
                    }
                    sb.append(compile(inputExpression.getText(), le));
                } else {
                    throw new IllegalStateException(
                            "Only LiteralExpressions handled at this time");
                }
            }
            sb.append(") { \n");
            List<LiteralExpression> conclusions = rule.getOutputEntry();

            for (int i = 0; i < conclusions.size(); i++) {
                if (i == 0) {
                    sb.append("  System.out.println('  match on rule \""
                            + ruleIdx + "\"');\n");
                }
                Expression ex = conclusions.get(i);
                if (ex instanceof LiteralExpression) {
                    LiteralExpression le = (LiteralExpression) ex;
                    sb.append("  ");
                    sb.append(dt.getOutputs().get(i).getOutputDefinition()
                            .getId());
                    if (isLiteral(le.getText())) {
                        sb.append(" = ");
                    }
                    sb.append(compile(le));
                    sb.append(";\n");

                    if (LOGGER.isDebugEnabled()) {
                        sb.append("  System.out.println('  "
                                + getRootObject(d.getInformationItem().getId())
                                + "'+JSON.stringify("
                                + getRootObject(d.getInformationItem().getId())
                                + "));\n");
                    }

                } else {
                    throw new IllegalStateException(
                            "Only LiteralExpressions handled at this time");
                }
            }
            if (dt.getHitPolicy() == HitPolicy.FIRST) {
                sb.append("  return;\n");
            }
            sb.append("} else { System.out.println('  no match on rule \""
                    + ruleIdx
                    + "\"'); }\n");
        }
        sb.append("}\n\n");

        // Make sure output is serialised
        sb.append("if (typeof " + d.getInformationItem().getId()
                + " == 'object')  " + d.getInformationItem().getId()
                + " = JSON.stringify(" + d.getInformationItem().getId()
                + ");\n");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sb.toString());
        }
        return sb.toString();
    }

    private boolean isLiteral(String expr) {
        char c = expr.trim().charAt(0); 

        if (OPERATORS.contains(c)) {
            return false;
        } else {
            return true;
        }
    }

    protected String createFunctionName(String id) {
        if (Character.isDigit(id.charAt(0))) {
            return "_" + id;
        } else {
            return id;
        }
    }

    protected String compile(LiteralExpression ex) {
        return compile("", ex);
    }

    protected String compile(String input, LiteralExpression ex) {
        Object expr = ex.getText();
		// Casting ought to be pretty safe, but who knows what will happen in
		// the future
        if (!(expr instanceof String)) {
			throw new DecisionException(
					String.format(
							"LiteralExpression is expected to be a String but was %1$s",
							expr.getClass().getName()));
		}
        return compile(input, (String) expr);
	}
	
    protected String compile(String input, String expr) {
		String rtn = expr;
        // TODO dmn11
        // if (!expr.startsWith(input)) {
        // rtn = input + "." + expr;
        // }
        for (DelExpression compiler : getDelExpressions()) {
            rtn = compiler.compile(rtn);
		}
		return rtn;
	}

}
