package io.onedecision.engine.decisions.model.dmn;

import java.util.List;

public class InputClause extends Clause {

    public InputClause(Expression expr, List<Expression> entries) {
        setInputExpression(expr);
        getInputEntry().addAll(entries);
    }

    public InputClause(Expression expr) {
        setInputExpression(expr);
    }
    
    public InputClause() {}

}
