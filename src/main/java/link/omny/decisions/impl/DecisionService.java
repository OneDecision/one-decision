package link.omny.decisions.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import link.omny.decisions.api.DecisionsException;
import link.omny.decisions.impl.del.DelExpression;
import link.omny.decisions.model.dmn.Clause;
import link.omny.decisions.model.dmn.Decision;
import link.omny.decisions.model.dmn.DecisionRule;
import link.omny.decisions.model.dmn.DecisionTable;
import link.omny.decisions.model.dmn.Expression;
import link.omny.decisions.model.dmn.LiteralExpression;
import link.omny.decisions.model.dmn.adapters.ExpressionAdapter;
import link.omny.decisions.model.dmn.adapters.ExpressionAdapter.AdaptedExpression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DecisionService {

	protected static final Logger LOGGER = LoggerFactory
			.getLogger(DecisionService.class);

	private static final List<String> EXCLUDED_OBJECTS = newArrayList(
			"context", "print", "println");

	protected List<DelExpression> compilers;

    private Map<String, String> cache = new HashMap<String, String>();
    private ScriptEngine jsEng;

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

	public List<DelExpression> getDelExpressions() {
		if (compilers == null) {
			compilers = new ArrayList<DelExpression>();
		}
		return compilers;
	}

	public void setDelExpressions(List<DelExpression> compilers) {
		this.compilers = compilers;
	}

    public Map<String, String> execute(Decision d, Map<String, String> params)
            throws DecisionsException {
        String script = getScript(d.getDecisionTable());

        for (Entry<String, String> o : params.entrySet()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("JSON input in Java: " + o);
			}
            jsEng.put(o.getKey(), o.getValue());
            try {
                Object r = jsEng.eval(script);
				LOGGER.debug("  response: " + r);
            } catch (ScriptException ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new DecisionsException("Unable to evaluate decision", ex);
            }
            for (Entry<String, Object> o2 : jsEng.getBindings(
                    ScriptContext.ENGINE_SCOPE).entrySet()) {
                if (!EXCLUDED_OBJECTS.contains(o2.getKey())) {
                    params.put(o2.getKey(), (String) o2.getValue());
                }
            }
        }
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("vars returned: " + params);
		}
        return params;
    }

    public String getScript(DecisionTable dt) throws DecisionsException {
        if (cache.containsKey(dt.getId())) {
            return cache.get(dt.getId());
        }

        StringBuilder sb = new StringBuilder();
        ExpressionAdapter adapter = new ExpressionAdapter();

        List<String> varsToInit = new ArrayList<String>();
        for (Clause o : dt.getClause()) {
            // System.out.println("  c: " + o);
            // for (InformationItem var : o.getInputVariables()) {
            // System.out.println("    var: " + var);
            // }
            // System.out.println("ie: " + inputExpression);
			if (o.getInputExpression() != null
					&& o.getInputExpression().getOnlyInputVariable() != null) {
                varsToInit.add(o.getInputExpression().getOnlyInputVariable()
                        .getName());
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
        for (DecisionRule rule : dt.getRule()) {
            List<Expression> conditions = rule.getConditions();
            for (int i = 0; i < conditions.size(); i++) {
                if (i == 0) {
                    sb.append("if (");
                } else {
                    sb.append(" && ");
                }

                Expression ex = conditions.get(i);

                if (ex instanceof LiteralExpression) {
					sb.append(compile((LiteralExpression) ex));
                } else if (ex instanceof AdaptedExpression) {
                    LiteralExpression le = (LiteralExpression) adapter
                            .unmarshal((AdaptedExpression) ex);
					sb.append(compile(le));
                } else {
                    throw new IllegalStateException(
                            "Only LiteralExpressions handled at this time");
                }
            }
            sb.append(") { \n");
            List<Expression> conclusions = rule.getConclusions();
            for (int i = 0; i < conclusions.size(); i++) {
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
            sb.append("}\n");
        }
        for (String var : varsToInit) {
            sb.append("if (typeof " + var + " == 'object')  " + var
                    + " = JSON.stringify(" + var + ");\n");
        }
        return sb.toString();
    }

	protected String compile(LiteralExpression ex) {
		Object expr = ex.getText().getContent().get(0);
		// Casting ought to be pretty safe, but who knows what will happen in
		// the future
		if (!(expr  instanceof String)) { 
			throw new DecisionsException(
					String.format(
							"LiteralExpression is expected to be a String but was %1$s",
							expr.getClass().getName()));
		}
		return compile((String) expr);
	}
	
	protected String compile(String expr) {
		String rtn = expr;
		for (DelExpression compiler : getDelExpressions()) {
			rtn = compiler.compile(expr);
		}
		return rtn;
	}
}
