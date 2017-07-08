package io.onedecision.engine.decisions.impl.del;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchAllExpression implements DelExpression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(MatchAllExpression.class);

    @Override
    public String compile(String expr, String input) {
        if ("-".equalsIgnoreCase(expr.trim())) {
            return "true";
        } else {
            return expr;
        }

    }
}
