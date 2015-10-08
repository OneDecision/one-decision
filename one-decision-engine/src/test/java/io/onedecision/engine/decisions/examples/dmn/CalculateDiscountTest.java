package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.DecisionService;
import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.impl.del.DurationExpression;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests a decision of the level of discount for a particular customer.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class CalculateDiscountTest implements ExamplesConstants {

    private static DecisionService svc;

    private static SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");

    private Map<String, Object> vars = new HashMap<String, Object>();

    private Number ordersize;

    private String customercategory;

    private Number totalOrderSum;

    private Date amountDue;

    @Parameters
    public static Collection<Object[]> data() throws ParseException {
        return Arrays.asList(new Object[][] { { "gold", 10, 2000,
                isoDate.parse("2015-10-31") } });
    }

    @BeforeClass
    public static void setUpClass() {
        svc = new DecisionService();
        List<DelExpression> compilers = new ArrayList<DelExpression>();
        compilers.add(new DurationExpression());
        svc.setDelExpressions(compilers);
    }

    public CalculateDiscountTest(String customercategory, Number ordersize,
            Number totalOrderSum, Date amountDue) {
        this.customercategory = customercategory;
        this.ordersize = ordersize;
        this.totalOrderSum = totalOrderSum;
        this.amountDue = amountDue;
    }

    @Test
    public void testCalculateDiscount() {
        try {
            vars.clear();
            vars.put("customercategory", customercategory);
            vars.put("ordersize", ordersize);
            vars = svc.executeDecision(CD_DEFINITION_ID, CD_DECISION_ID, vars,
                    TENANT_ID);
            assertEquals(amountDue, vars.get("amountDue"));
            assertEquals(totalOrderSum, vars.get("totalOrderSum"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

}
