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

import link.omny.decisions.api.DecisionException;
import link.omny.decisions.model.Clause;
import link.omny.decisions.model.Decision;
import link.omny.decisions.model.DecisionRule;
import link.omny.decisions.model.DecisionTable;
import link.omny.decisions.model.Expression;
import link.omny.decisions.model.LiteralExpression;
import link.omny.decisions.model.adapters.ExpressionAdapter;
import link.omny.decisions.model.adapters.ExpressionAdapter.AdaptedExpression;

import org.springframework.stereotype.Component;

@Component
public class DecisionService {

    private Map<String, String> cache = new HashMap<String, String>();
    private ScriptEngine jsEng;
    private static final List<String> EXCLUDED_OBJECTS = newArrayList(
            "context", "print", "println");

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

    public Map<String, String> execute(Decision d, Map<String, String> params)
            throws DecisionException {
        String script = getScript(d.getDecisionTable());

        for (Entry<String, String> o : params.entrySet()) {
            System.out.println("JSON input in Java: " + o);
            jsEng.put(o.getKey(), o.getValue());
            try {
                Object r = jsEng.eval(script);
                System.out.println("  response: " + r);
            } catch (ScriptException ex) {
                ex.printStackTrace();
            }
            for (Entry<String, Object> o2 : jsEng.getBindings(
                    ScriptContext.ENGINE_SCOPE).entrySet()) {
                if (!EXCLUDED_OBJECTS.contains(o2.getKey())) {
                    params.put(o2.getKey(), (String) o2.getValue());
                }
            }
        }
        System.out.println("vars returned: " + params);
        return params;
    }

    public String getScript(DecisionTable dt) throws DecisionException {
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
            if (o.getInputExpression() != null) {
                varsToInit.add(o.getInputExpression().getOnlyInputVariable()
                        .getName());
            }

            if (o.getOutputDefinition() != null) {
                varsToInit.add(o.getOutputDefinition().getLocalPart());
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
                    sb.append(((LiteralExpression) ex).getText().getContent()
                            .get(0));
                } else if (ex instanceof AdaptedExpression) {
                    LiteralExpression le = (LiteralExpression) adapter
                            .unmarshal((AdaptedExpression) ex);
                    sb.append(le.getText().getContent().get(0));
                } else {
                    // TODO
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
                    sb.append(((LiteralExpression) ex).getText().getContent()
                            .get(0));
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
}
