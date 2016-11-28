package io.onedecision.engine.decisions.model.dmn;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class UnaryTestsTest {

    private static ObjectFactory objFact;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        objFact = new ObjectFactory();
    }

    @Test
    public void testParseText() {
        String val1 = "EMPLOYED";
        String val2 = "UNEMPLOYED";

        UnaryTests unaryTests = objFact.createUnaryTests().withUnaryTests(val1,
                val2);
        assertEquals(2, unaryTests.getUnaryTests().size());
        assertEquals("\"EMPLOYED\"", unaryTests.getUnaryTests().get(0));
        assertEquals("\"UNEMPLOYED\"", unaryTests.getUnaryTests().get(1));
    }

    @Test
    public void testParseQuotedText() {
        String val1 = "\"EMPLOYED\"";
        String val2 = "\"UNEMPLOYED\"";

        UnaryTests unaryTests = objFact.createUnaryTests().withUnaryTests(val1,
                val2);
        assertEquals(2, unaryTests.getUnaryTests().size());
        assertEquals("\"EMPLOYED\"", unaryTests.getUnaryTests().get(0));
        assertEquals("\"UNEMPLOYED\"", unaryTests.getUnaryTests().get(1));
    }

    @Test
    public void testParseNumbers() {
        String val1 = "1";
        String val2 = "2";

        UnaryTests unaryTests = objFact.createUnaryTests().withUnaryTests(val1,
                val2);
        assertEquals(2, unaryTests.getUnaryTests().size());
        assertEquals("1", unaryTests.getUnaryTests().get(0));
        assertEquals("2", unaryTests.getUnaryTests().get(1));
    }

}
