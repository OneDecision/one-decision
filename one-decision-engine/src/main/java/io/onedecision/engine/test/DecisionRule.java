package io.onedecision.engine.test;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.DecisionService;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.impl.del.DurationExpression;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Implement JUnit rule wrapper to inject <code>DecisionEngine</code> into
 * tests.
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * public class DecisionTest {
 * 
 *   &#64;Rule
 *   public DecisionRule decisionRule = new DecisionRule();
 *   
 *   ...
 * }
 * </pre>
 * 
 * <p>
 * You can declare a deployment with the {@link Deployment} annotation. This
 * base class will make sure that this deployment gets deployed before the setUp
 * and removed after the tearDown.
 * </p>
 * 
 * @author Tim Stephenson
 */
public class DecisionRule implements TestRule {

    protected String tenantId;

    protected DecisionEngine de;

    protected List<String> definitionIds = new ArrayList<String>();

    public DecisionRule() {
    }

    public DecisionRule(DecisionEngine decisionEngine) {
        setDecisionEngine(decisionEngine);
    }

    /**
     * Implementation based on {@link TestWatcher}.
     */
    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                List<Throwable> errors = new ArrayList<Throwable>();

                startingQuietly(description, errors);
                try {
                    base.evaluate();
                    succeededQuietly(description, errors);
                } catch (AssumptionViolatedException e) {
                    errors.add(e);
                    skippedQuietly(e, description, errors);
                } catch (Throwable t) {
                    errors.add(t);
                    failedQuietly(t, description, errors);
                } finally {
                    finishedQuietly(description, errors);
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    private void succeededQuietly(Description description,
            List<Throwable> errors) {
        try {
            succeeded(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void failedQuietly(Throwable t, Description description,
            List<Throwable> errors) {
        try {
            failed(t, description);
        } catch (Throwable t1) {
            errors.add(t1);
        }
    }

    private void skippedQuietly(AssumptionViolatedException e,
            Description description, List<Throwable> errors) {
        try {
            skipped(e, description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void startingQuietly(Description description, List<Throwable> errors) {
        try {
            starting(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void finishedQuietly(Description description, List<Throwable> errors) {
        try {
            finished(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    /**
     * Invoked when a test succeeds
     */
    protected void succeeded(Description description) {
    }

    /**
     * Invoked when a test fails
     */
    protected void failed(Throwable e, Description description) {
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected void skipped(AssumptionViolatedException e,
            Description description) {
    }

    protected void starting(Description description) throws IOException {
        if (de == null) {
            initializeDecisionEngine();
        }

        // Allow for mock configuration
        configureDecisionEngine();

        // Load DMN resources into engine
        Deployment deployment = description.getAnnotation(Deployment.class);
        tenantId = deployment.tenantId();
        for (String resource : deployment.resources()) {
            String dmnXml = loadFromClassPath(resource);
            DmnModel dmnModel = new DmnModel(dmnXml, null, null, tenantId);
            definitionIds.add(dmnModel.getDefinitionId());
            de.getRepositoryService().createModelForTenant(dmnModel);
        }

    }

    public static String loadFromClassPath(String resource) {
        InputStream is = null;
        try {
            is = DecisionRule.class.getResourceAsStream(resource);
            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    protected void initializeDecisionEngine() {
        de = new InMemoryDecisionEngineImpl();

        List<DelExpression> compilers = new ArrayList<DelExpression>();
        compilers.add(new DurationExpression());
        ((DecisionService) de.getRuntimeService()).setDelExpressions(compilers);
    }

    protected void configureDecisionEngine() {
        /** meant to be overridden */
    }

    protected void finished(Description description) {
        for (String definitionId : definitionIds) {
            de.getRepositoryService().deleteModelForTenant(definitionId,
                    tenantId);
        }
    }

    public DecisionEngine getDecisionEngine() {
        return de;
    }

    public void setDecisionEngine(DecisionEngine de) {
        this.de = de;
    }

}
