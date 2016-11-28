package io.onedecision.engine.decisions.model.dmn;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

public class DecisionRuleTest {
    private static ObjectFactory objFact;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        objFact = new ObjectFactory();
    }

    @Test
    public void testWithStringInputOutputEntries() {
        DecisionRule rule = objFact.createDecisionRule()
                .withInputEntries("HIGH", "MEDIUM")
                .withOutputEntries("FULL");
        assertEquals(1, rule.getInputEntry().size());
        assertEquals("\"HIGH\",\"MEDIUM\"", rule.getInputEntry().get(0)
                .getText());
        assertEquals(1, rule.getOutputEntry().size());
        assertEquals("\"FULL\"", rule.getOutputEntry().get(0).getText());
    }

    @Test
    public void testWithBooleanInputOutputEntries() {
        DecisionRule rule = objFact.createDecisionRule()
                .withInputEntries(true, false).withOutputEntries(Boolean.FALSE);
        assertEquals(1, rule.getInputEntry().size());
        assertEquals("true,false", rule.getInputEntry().get(0).getText());
        assertEquals(1, rule.getOutputEntry().size());
        assertEquals("false", rule.getOutputEntry().get(0).getText());
    }

    @Test
    public void testWithNumberInputOutputEntries() {
        DecisionRule rule = objFact.createDecisionRule()
                .withInputEntries(10, 20)
                .withOutputEntries(new BigDecimal(30));
        assertEquals(1, rule.getInputEntry().size());
        assertEquals("10,20", rule.getInputEntry().get(0)
                .getText());
        assertEquals(1, rule.getOutputEntry().size());
        assertEquals("30", rule.getOutputEntry().get(0).getText());
    }

    @Test
    public void testWithExpressionInputOutputEntries() {
        DecisionRule rule = objFact.createDecisionRule()
                .withInputEntries("[0..100]")
                .withOutputEntries("a + b + c");
        assertEquals(1, rule.getInputEntry().size());
        assertEquals("[0..100]", rule.getInputEntry().get(0).getText());
        assertEquals(1, rule.getOutputEntry().size());
        assertEquals("a + b + c", rule.getOutputEntry().get(0).getText());
    }
}
