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
                { "x", "&lt; PT1H", String.format("x &lt; %1$d", ONE_HOUR) },
                { "x", "&lt; PT1M", String.format("x &lt; %1$d", ONE_MIN) },
                { "x", "&lt; P1D", String.format("x &lt; %1$d", ONE_DAY) },
                { "x", "&lt; P1W", String.format("x &lt; %1$d", ONE_DAY * 7) },
                { "x", "&lt; P10W", String.format("x &lt; %1$d", ONE_DAY * 70) },
                { "x", "&lt; P52W", String.format("x &lt; %1$d", ONE_DAY * 7 * 52) },
                { "x", "&lt; P1Y", String.format("x &lt; %1$d", ONE_DAY * 365) },
                { 
                        "contact.timeSinceBusinessPlanDownload",
                        "&gt;= P1W",
                        "contact.timeSinceBusinessPlanDownload &gt;= "
                                + (ONE_DAY * 7) 
                } 
        });
    }

    private String varName;
    private String duration;
    private String durationMillis;
    private DurationExpression de = new DurationExpression();

    public DurationExpressionTest(String varName, String duration,
            String durationMillis) {
        this.varName = varName;
        this.duration = duration;
        this.durationMillis = durationMillis;
    }

    @Test
    public void test() {
        assertEquals(durationMillis, de.compile(duration, varName));
    }

}
