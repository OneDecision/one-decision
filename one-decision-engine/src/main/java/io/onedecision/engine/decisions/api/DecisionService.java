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

import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.model.dmn.Clause;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionModelImport;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.adapters.ExpressionAdapter;
import io.onedecision.engine.decisions.model.dmn.adapters.ExpressionAdapter.AdaptedExpression;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionService implements DecisionConstants {

    private static final String CP_PROTOCOL = "classpath://";

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionService.class);

	private static final List<String> EXCLUDED_OBJECTS = newArrayList(
            "context", "print", "println", "System");

	protected List<DelExpression> compilers;

    private Map<String, String> cache = new HashMap<String, String>();
    private ScriptEngine jsEng;

    private ScriptEngineManager sem;

    private static List<String> newArrayList(String... objects) {
        List<String> list = new ArrayList<String>();
        for (String s : objects) {
            list.add(s);
        }
        return Collections.unmodifiableList(list);
    }

    public DecisionService() {
        sem = new ScriptEngineManager();
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

    public Map<String, Object> execute(Definitions dm, String decisionId,
            Map<String, Object> vars) throws DecisionException {
        String script = getScript(dm, decisionId);
        Map<String, Object> results = execute(dm.getDecisionById(decisionId),
                script, vars);

        Map<String, Object> limitedResults = new HashMap<String, Object>();
        for (InformationItem item : dm.getInformationItems()) {
            limitedResults.put(item.getId(), results.get(item.getId()));
        }

        return limitedResults;
    }

    public Map<String, Object> execute(Decision d, Map<String, Object> params)
            throws DecisionException {
        String script = getScript(d.getDecisionTable());
        return execute(d, script, params);
    }

    protected Map<String, Object> execute(Decision d,
            String script, Map<String, Object> params) throws DecisionException {
        jsEng = sem.getEngineByName("JavaScript");
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

        // TODO should be able to limit return to decision's own information
        // items
        for (Entry<String, Object> o2 : jsEng.getBindings(
                ScriptContext.ENGINE_SCOPE).entrySet()) {
            if (!EXCLUDED_OBJECTS.contains(o2.getKey())) {
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
        
        for (DecisionModelImport import_ : dm.getImport()) {
            if (EXPR_URI_JS.equals(import_.getImportType())) {
                if (import_.getLocationURI().startsWith(CP_PROTOCOL)) {
                    InputStream is = null;
                    try {
                        String resource = import_.getLocationURI().substring(
                                CP_PROTOCOL.length());
                        is = getClass().getResourceAsStream(resource);
                        sb.append('\n')
                            .append(new Scanner(is).useDelimiter("\\A").next())
                            .append('\n');
                    } catch (Exception e) {
                        LOGGER.error(String.format(
                                "Unable to load classpath resource: %1$s",
                                import_.getLocationURI()), e);
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                } else {
                    sb.append("load('" + import_.getLocationURI() + "');\n");
                }
            }
        }
        
        return getScript(sb, dm.getDecisionById(decisionId).getDecisionTable());
    }

    public String getScript(DecisionTable dt) throws DecisionException {
        StringBuilder sb = new StringBuilder();
        return getScript(sb, dt);
    }

    protected String getScript(StringBuilder sb, DecisionTable dt) {
        if (cache.containsKey(dt.getId())) {
            return cache.get(dt.getId());
        }

        // Rhino _and_ Nashorn compatible way to enable access to println
        sb.append("var System = java.lang.System;\n");
        ExpressionAdapter adapter = new ExpressionAdapter();

        Set<String> varsToInit = new HashSet<String>();
        for (Clause o : dt.getClause()) {
			if (o.getInputExpression() != null
                    && !o.getInputExpression().getInputVariable().isEmpty()) {
                varsToInit.add(o.getInputExpression().getOnlyInputVariable()
                        .getId());
			} else {
				LOGGER.debug(String.format(
						"clause %1$s does not have an input expression",
						o.getName()));
            }

            if (o.getOutputDefinition() != null) {
                varsToInit.add(o.getOutputDefinition().getLocalPart());
			} else {
				LOGGER.debug(String.format(
						"clause %1$s does not have an output definition",
						o.getName()));
            }
        }
        for (String var : varsToInit) {
            sb.append("if (" + var + " == undefined) " + var + " = {};\n");
            // sb.append("println(" + var + ");\n");
            sb.append("if (typeof " + var + " == 'string') var " + var
                    + " = JSON.parse(" + var + ");\n");
        }
        sb.append(createFunctionName(dt.getId())).append("();\n\n");
        
        sb.append("function ").append(createFunctionName(dt.getId()))
                .append("() {\n");
        int ruleIdx = 0;
        for (DecisionRule rule : dt.getRule()) {
            ruleIdx++;
            List<Expression> conditions = rule.getConditions();
            for (int i = 0; i < conditions.size(); i++) {
                if (i == 0) {
                    sb.append("if (");
                } else {
                    sb.append(" && ");
                }
                Expression ex = conditions.get(i);
                Clause clause = dt.findClauseFromInputEntry(ex);
                
                if (ex instanceof LiteralExpression) {
                    sb.append(compile(clause.getInputExpressionId(),
                            (LiteralExpression) ex));
                } else if (ex instanceof AdaptedExpression) {
                    LiteralExpression le = (LiteralExpression) adapter
                            .unmarshal((AdaptedExpression) ex);
                    sb.append(compile(clause.getInputExpressionId(), le));
                } else {
                    throw new IllegalStateException(
                            "Only LiteralExpressions handled at this time");
                }
            }
            sb.append(") { \n");
            List<Expression> conclusions = rule.getConclusions();

            for (int i = 0; i < conclusions.size(); i++) {
                if (i == 0) {
                    sb.append("  System.out.println('  match on rule \""
                            + ruleIdx + "\"');\n");
                }
                Expression ex = conclusions.get(i);
                if (ex instanceof LiteralExpression) {
                    sb.append("  ");
					sb.append(compile((LiteralExpression) ex));
                    sb.append(";\n");
                } else if (ex instanceof AdaptedExpression) {
                    sb.append("  ");
                    LiteralExpression le = (LiteralExpression) adapter
                            .unmarshal((AdaptedExpression) ex);
                    sb.append(le.getText().getContent().get(0));
                    sb.append(";\n");
                } else {
                    // TODO
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
        sb.append("}");

        for (String var : varsToInit) {
            sb.append("if (typeof " + var + " == 'object')  " + var
                    + " = JSON.stringify(" + var + ");\n");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sb.toString());
        }
        return sb.toString();
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
		Object expr = ex.getText().getContent().get(0);
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
        if (!expr.startsWith(input) && !isFunction(expr)) {
             rtn = input + "." + expr;
        }
        for (DelExpression compiler : getDelExpressions()) {
            rtn = compiler.compile(rtn);
		}
		return rtn;
	}

    private boolean isFunction(String input) {
        if (input.indexOf('(') == -1 && input.indexOf(')') == -1) {
            return false;
        }else {
            return true;
        }
    }
}
