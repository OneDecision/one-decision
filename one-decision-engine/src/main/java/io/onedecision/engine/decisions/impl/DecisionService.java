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
import io.onedecision.engine.decisions.impl.del.DurationExpression;
import io.onedecision.engine.decisions.impl.del.MatchAllExpression;
import io.onedecision.engine.decisions.impl.del.RangeExpression;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.Import;
import io.onedecision.engine.decisions.model.dmn.InputClause;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.UnaryTests;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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

    protected List<DelExpression> statementCompilers;

    /**
     * Cache scripts, keyed by decision id.
     */
    private Map<String, String> cache = new HashMap<String, String>();

    private static final String CRLF = System.getProperty("line.separator");
    private static final String INDENT = "  ";

    private ScriptEngineManager sem;
            
    public DecisionService() {
        sem = new ScriptEngineManager();


        List<DelExpression> compilers = new ArrayList<DelExpression>();
        compilers.add(new DurationExpression());
        compilers.add(new RangeExpression());
        compilers.add(new MatchAllExpression());
        compilers
                .add(new io.onedecision.engine.decisions.impl.del.LiteralExpression());
        setDelExpressions(compilers);

        List<DelExpression> statementCompilers = new ArrayList<DelExpression>();
        statementCompilers.add(new DurationExpression());
        statementCompilers.add(new RangeExpression());
        // statementCompilers.add(new MatchAllExpression());
        setDelStatementExpressions(statementCompilers);
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

    public List<DelExpression> getDelStatementExpressions() {
        if (statementCompilers == null) {
            statementCompilers = new ArrayList<DelExpression>();
        }
        return statementCompilers;
    }

	public void setDelExpressions(List<DelExpression> compilers) {
		this.compilers = compilers;
	}

    public void setDelStatementExpressions(
            List<DelExpression> statementCompilers) {
        this.statementCompilers = statementCompilers;
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
        Decision decision = dm.getDecision(decisionId);

        StringBuilder sb = new StringBuilder();
        Expression expr = decision.getExpression().getValue();
        if (expr instanceof DecisionTable) {
            for (InputClause input : ((DecisionTable) expr)
                    .getInputs()) {
                String varName = input.getInputExpression().getText();
                if (varName != null && varName.indexOf(".") != -1) {
                    varName = varName.substring(0, varName.indexOf("."));
                }
                if (!vars.keySet().contains(varName)) {
                    sb.append(varName).append(",");
                }
            }
        } else {
            LOGGER.debug(String.format(
                    "Do not know how to check inputs for expression type %1$s",
                    expr.getClass().getName()));
        }

        if (sb.length() > 0) {
            throw new DecisionException(String.format(
                    "Missing information requirement(s): %1$s", sb.toString()));
        }

        String script = getScript(dm, decisionId);

        Map<String, Object> results = execute(decision,
                script, vars);

        return Collections.singletonMap(decision.getVariable().getName(),
                results.get(decision.getVariable().getName()));
    }

    protected Map<String, Object> execute(Decision d,
            String script, Map<String, Object> params) throws DecisionException {
        ScriptEngine jsEng = sem.getEngineByName("JavaScript");

        for (Entry<String, Object> o : params.entrySet()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("JSON input in Java: " + o);
			}

            Object val = o.getValue();
            if (val instanceof String) {
                try {
                    val = Double.parseDouble((String) o.getValue());
                    jsEng.put(o.getKey(), val);
                } catch (NumberFormatException e) {
                    LOGGER.debug(String.format("Value %1$s is NaN",
                            o.getValue()));
                    if ("true".equalsIgnoreCase((String) val)
                            || "false".equalsIgnoreCase((String) val)) {
                        jsEng.put(o.getKey(),
                                Boolean.parseBoolean((String) val));
                    } else if (((String) val).startsWith("{")
                            || ((String) val).startsWith("[")) {
                        jsEng.put(getRootObject(o.getKey()), val);
                    } else {
                        jsEng.put(o.getKey(), val);
                    }
                }
            } else {
                jsEng.put(o.getKey(), val);
            }
        }

        try {
            Object r = jsEng.eval(script);
            LOGGER.debug("  response: " + r);
        } catch (ScriptException ex) {
            LOGGER.error(ex.getMessage());
            throw new DecisionException(String.format(
                    "Unable to evaluate decision, cause was: %1$s",
                    ex.getMessage()));
        }

        // TODO rather than placing return type into params map, could return a
        // single object?
        for (Entry<String, Object> o2 : jsEng.getBindings(
                ScriptContext.ENGINE_SCOPE).entrySet()) {
            if (o2.getKey().equals(d.getVariable().getName())) {
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

        if (System.getProperty("java.version").startsWith("1.6")
                || System.getProperty("java.version").startsWith("1.7")) {
            loadLibrariesForRhino(dm, sb);
        } else {
            loadLibrariesForNashorn(dm, sb);
        }

        // init vars
        // root objects must be listed as InputData at the Definitions level
        // but we'll leave that as a task for the validator for now.
        Decision d = dm.getDecision(decisionId); 
        for (InputClause input : d.getDecisionTable().getInputs()) {
            String varName = input.getInputExpression().getText();
            // if (varName.indexOf('.') >= 0) {
            // sb.append(String.format("initObj('%1$s');",
            // varName)).append(CRLF);

            String rootObject = getRootObject(input.getInputExpression()
                    .getText());
            // sb.append(
            // "if (" + rootObject + "==undefined) var " + rootObject
            // + " = {};").append(CRLF);
                sb.append(
                        "if (typeof " + rootObject + "=='string' && "
                                + rootObject + ".charAt(0)=='{') " + rootObject
                                + " = JSON.parse(" + rootObject + ");").append(
                        CRLF);
                // } else {
                // sb.append(
                // "if (" + varName + "==undefined) var " + varName + ";")
                // .append(CRLF);
            // }
            if (LOGGER.isDebugEnabled()) {
                sb.append(
                        String.format(
                                "console.log('// input %1$s = '+JSON.stringify(%2$s));",
                                varName, rootObject)).append(CRLF);
            }
        }

        // if (cache.containsKey(d.getId())) {
        // return cache.get(d.getId());
        // } else {
            return getScript(sb, d);
        // }
    }

    private void loadLibrariesForNashorn(Definitions dm, StringBuilder sb) {
        sb.append(
                "load('classpath:io/onedecision/engine/decisions/impl/functions.js');")
                .append(CRLF);
        for (Import import_ : dm.getImports()) {
            if (EXPR_URI_JS.equals(import_.getImportType())) {
                sb.append("load('" + import_.getLocationURI() + "');").append(
                        CRLF);
            }
        }
    }

    private void loadLibrariesForRhino(Definitions dm, StringBuilder sb) {
        sb.append(
                "var console = { log: function(msg) { java.lang.System.out.println(msg); } };")
                .append(CRLF);
        sb.append(
                loadScriptFromClassPath("/io/onedecision/engine/decisions/impl/functions.js"))
                .append(CRLF);
        for (Import import_ : dm.getImports()) {
            if (EXPR_URI_JS.equals(import_.getImportType())) {
                String resource = import_.getLocationURI();
                if (!resource.startsWith("/")) {
                    resource = "/" + resource;
                }
                sb.append(
                        loadScriptFromClassPath("/io/onedecision/engine/decisions/impl/functions.js"))
                        .append(CRLF);
            }
        }
    }

    protected String getRootObject(String text) {
        if (text.indexOf('.') != -1) {
            return text.substring(0, text.indexOf('.'));
        }
        return text;
    }

    protected String getScript(StringBuilder sb, Decision d) {
        DecisionTable dt = d.getDecisionTable();

        String functionName = createFunctionName(d.getName());
        String outputVar = d.getVariable().getName();
        sb.append(String.format("var %1$s = %2$s();%3$s%3$s", outputVar,
                functionName, CRLF));

        sb.append("function ").append(functionName).append("() {").append(CRLF);

        int ruleIdx = 0;
        for (DecisionRule rule : dt.getRules()) {
            ruleIdx++;
            List<UnaryTests> conditions = rule.getInputEntry();
            for (int i = 0; i < conditions.size(); i++) {
                LiteralExpression inputExpression = dt.getInputs().get(i)
                        .getInputExpression();
                if (i == 0) {
                    sb.append(INDENT).append("if (");
                } else {
                    sb.append(" && ");
                }
                UnaryTests ex = conditions.get(i);
                // if (isLiteral(ex.getText()) || isBoolean(ex.getText())) {
                // sb.append(inputExpression.getText()).append(" == ");
                // } else {

                    sb.append(compile(inputExpression.getText(), ex));
                // }
            }
            sb.append(") { ").append(CRLF);
            List<LiteralExpression> conclusions = rule.getOutputEntry();

            for (int i = 0; i < conclusions.size(); i++) {
                if (i == 0) {
                    sb.append(INDENT)
                            .append("  console.log('  match on rule \""
                                    + ruleIdx + "\"');").append(CRLF);
                }
                Expression ex = conclusions.get(i);
                if (ex instanceof LiteralExpression) {
                    String varName = dt.getOutputs().get(i).getName();
                    sb.append(INDENT).append(INDENT)
                            .append(String.format("initObj('%1$s');", varName))
                            .append(CRLF);
                    LiteralExpression le = (LiteralExpression) ex;
                    sb.append(INDENT).append(INDENT);

                    sb.append(IdHelper.toIdentifier(varName));
                    // TODO Non-string literal assignment
//                    if (isLiteral(le.getText())) {
                        sb.append(" = ");
//                    } else {
                    sb.append(compileAssignment(
IdHelper.toIdentifier(varName),
                            le));
//                    }
                    sb.append(";").append(CRLF);

                    if (LOGGER.isDebugEnabled()) {
                        sb.append(INDENT)
                                .append(String
                                        .format("  console.log('  %1$s: '+JSON.stringify(%1$s));",
                                                getRootObject(outputVar)))
                                .append(CRLF);
                    }

                } else {
                    throw new IllegalStateException(
                            "Only LiteralExpressions handled at this time");
                }
            }
            if (dt.getHitPolicy() == HitPolicy.FIRST) {
                sb.append(INDENT)
                        .append(INDENT)
                        .append(String.format("return %1$s;%2$s", outputVar,
                                CRLF));
            }
            sb.append(INDENT)
                    .append("} else { console.log('  no match on rule \""
                    + ruleIdx
 + "\"'); }").append(CRLF);
        }
        sb.append(INDENT)
                .append(String.format("return %1$s;%2$s", outputVar,
                        CRLF)).append("}").append(CRLF).append(CRLF);

        // Make sure output is serialised
        sb.append(
                "if (typeof " + outputVar + " == 'object')  " + outputVar
                        + " = JSON.stringify(" + outputVar
 + ");")
                .append(CRLF)
;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sb.toString());
        }
        synchronized (cache) {
            cache.put(d.getId(), sb.toString());
        }
        return sb.toString();
    }

    private boolean isLiteral(String expr) {
        expr = expr.trim();
        return expr.startsWith("\"") && expr.endsWith("\"");
        // char c = expr.trim().charAt(0);
        //
        // if (OPERATORS.contains(c)) {
        // return false;
        // } else {
        // return true;
        // }
    }

    protected String createFunctionName(String id) {
        if (Character.isDigit(id.charAt(0))) {
            return "_" + IdHelper.toIdentifier(id);
        } else {
            return IdHelper.toIdentifier(id);
        }
    }

    protected String compile(String input, UnaryTests ex) {
        return compile(input, ex.getText());
    }

    // protected String compile(LiteralExpression ex) {
    // return compile("", ex);
    // }

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
        for (DelExpression compiler : getDelExpressions()) {
            rtn = compiler.compile(rtn, input);
            if (!rtn.equals(expr)) {
                // compiler handled this expr, skip others
                break;
            }
		}
		return rtn;
	}

    protected String compileAssignment(String input, LiteralExpression expr) {
        String rtn = expr.getText();
        for (DelExpression compiler : getDelStatementExpressions()) {
            rtn = compiler.compile(rtn, input);
            if (!rtn.equals(expr)) {
                // compiler handled this expr, skip others
                break;
            }
        }
        return rtn;
    }

    public static String loadScriptFromClassPath(String resource) {
        InputStream is = null;
        try {
            is = DecisionRule.class.getResourceAsStream(resource);
            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }
}
