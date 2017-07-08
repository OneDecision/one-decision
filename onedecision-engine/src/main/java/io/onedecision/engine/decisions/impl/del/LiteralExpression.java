package io.onedecision.engine.decisions.impl.del;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteralExpression implements DelExpression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(LiteralExpression.class);

    private static final List<Character> OPERATORS = Arrays
            .asList(new Character[] { '<', '=', '!', '>' });

    @Override
    public String compile(String expr, String input) {
        char c = expr.trim().charAt(0);

        if (OPERATORS.contains(c)) {
            return String.format("%1$s %2$s", input, expr);
        } else {
            return String.format("%1$s == %2$s", input, expr);
        }

    }
}
