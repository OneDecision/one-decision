package link.omny.decisions.impl.experimental;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import link.omny.decisions.impl.experimental.DurationDecisionExpression;
import link.omny.decisions.impl.experimental.Operator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DurationDecisionExpressionTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        DurationDecisionExpression expr = new DurationDecisionExpression(
                "< P7D");
        assertEquals(Operator.LT, expr.getOperator());
        assertEquals(1000 * 60 * 60 * 24 * 7, expr.getDuration().getMillis());

        Object bean = new TestObject();
        assertTrue(expr.isTrue(bean, "timeSinceXxx"));
    }

    class TestObject {

        long timeSinceXxx() {
            return new Date().getTime();
        }
    }

}
