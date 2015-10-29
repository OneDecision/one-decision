package io.onedecision.engine.decisions.examples.ch11;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;

import org.junit.BeforeClass;
import org.junit.Test;

public class Ch11LoanExampleTest implements ExamplesConstants {

    private static Ch11LoanExample ch11LoanExample;

    private static DecisionEngine de;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ch11LoanExample = new Ch11LoanExample();
        de = new InMemoryDecisionEngineImpl();
    }

    @Test
    public void testSerialization() throws Exception {
        de.getRepositoryService().createModelForTenant(
                ch11LoanExample.getDmnModel());
    }

}
