package io.onedecision.engine.decisions.impl.del;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RangeExpressionTest {
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                { "x", "[2..9]" },
                { "x","(2..9]" },
                { "x","[2..9)" },
                { "x","(2..9)" },
        });
    }

    private String varName;
    private String rangeExpr;
    private String output;
    private RangeExpression de = new RangeExpression();

    public RangeExpressionTest(String varName, String rangeExpr) {
        this.varName = varName;
        this.rangeExpr = rangeExpr;
        this.output = String.format("parseInt(%1$s).inRange('%2$s')", varName,
                rangeExpr);
    }

    @Test
    public void test() {
        assertEquals(output, de.compile(rangeExpr, varName));
    }

}
