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
public class DurationExpressionTest {
    private static final long ONE_MIN = 60 * 1000;
    private static final long ONE_HOUR = 60 * ONE_MIN;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                { "PT1H", String.valueOf(ONE_HOUR) },
                { "PT1M", String.valueOf(ONE_MIN) },
                { "P1D", String.valueOf(ONE_DAY) },
                { "P1W", String.valueOf(ONE_DAY * 7) },
                { "P10W", String.valueOf(ONE_DAY * 70) },
                { "P52W", String.valueOf(ONE_DAY * 7 * 52) },
                { "P1Y", String.valueOf(ONE_DAY * 365) },
                {
                        "contact.timeSinceBusinessPlanDownload &gt;= P1W",
                        "contact.timeSinceBusinessPlanDownload &gt;= "
                                + (ONE_DAY * 7) },
                {
                        "foo == P1W && bar == PT1H",
                        "foo == " + String.valueOf(ONE_DAY * 7) + " && bar == "
                                        + String.valueOf(ONE_HOUR) },
                {
                        "contact.timeSinceValuation > PT1H && contact.timeSinceValuation < P1W",
                        "contact.timeSinceValuation > "
                                + String.valueOf(ONE_HOUR)
                                + " && contact.timeSinceValuation < "
                                + String.valueOf(ONE_DAY * 7) }
        });
    }

    private String duration;
    private String durationMillis;
    private DurationExpression de = new DurationExpression();

    public DurationExpressionTest(String duration, String durationMillis) {
        this.duration = duration;
        this.durationMillis = durationMillis;
    }

    @Test
    public void test() {
        assertEquals(durationMillis, de.compile(duration));
    }

}
