package io.onedecision.engine.decisions.impl.del;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangeExpression implements DelExpression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(RangeExpression.class);

    @Override
    public String compile(String script, String input) {
        Pattern pattern = Pattern.compile("^[\\[\\(].*[\\]\\)]$");
        Matcher matcher = pattern.matcher(script);
        if (matcher.matches() && input.indexOf('.') != -1) {
            return String.format("parseFloat(%1$s).inRange('%2$s')", input,
                    script);
        } else if (matcher.matches()) {
            return String.format("parseInt(%1$s).inRange('%2$s')", input,
                    script);
        } else {
            return script;
        }
    }
}
