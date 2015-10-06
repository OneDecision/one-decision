package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Clause;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;

import org.junit.BeforeClass;
import org.junit.Test;

public class DecisionServiceTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testCompileInputExpressionRefItemDefinition() {
        Clause clause = new Clause();
        LiteralExpression inputExpression = new LiteralExpression();
        // inputExpression.setItemDefinition(value)
        clause.setInputExpression(inputExpression);
    }

}
