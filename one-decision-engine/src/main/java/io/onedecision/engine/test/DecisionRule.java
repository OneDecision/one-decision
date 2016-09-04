package io.onedecision.engine.test;

import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.DecisionService;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.impl.del.DurationExpression;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.validation.ConstraintViolation;

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

    public static final File outputDir = new File("target", "decisions");

    protected String tenantId;

    protected DecisionEngine de;

    protected List<String> definitionIds = new ArrayList<String>();

    private ObjectFactory objFact;

    public DecisionRule() {
        ;
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

        // TODO Should this be part of repositoryService?
        objFact = new ObjectFactory();

        outputDir.mkdirs();

        // Allow for mock configuration
        configureDecisionEngine();

        Deployment deployment = description.getAnnotation(Deployment.class);
        if (deployment != null) {
            loadResourcesIntoEngine(deployment, description);
        }
    }

    private void loadResourcesIntoEngine(Deployment deployment,
            Description description) throws IOException {
        tenantId = deployment.tenantId();
        for (String resource : deployment.resources()) {
            String model = loadFromClassPath(resource);
            DmnModel dmnModel = null;
            if (resource.toLowerCase().endsWith(".dmn")) {
                dmnModel = new DmnModel(model, null, null, tenantId);
            } else {
                throw new IllegalArgumentException(
                        "Only know how to convert resources with .dmn extensions");
            }
            definitionIds.add(dmnModel.getDefinitionId());
            Set<ConstraintViolation<Definitions>> violations = de
                    .getRepositoryService().validate(dmnModel.getDefinitions());
            if (violations.isEmpty()) {
                de.getRepositoryService().createModelForTenant(dmnModel);
            } else {
                for (ConstraintViolation<Definitions> violation : violations) {
                    System.err.println(violation.getMessage());
                }
                String msg = "Resource is not valid, violations have been written to System.err";
                fail(msg);
                throw new IllegalArgumentException(msg);
            }
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

    public ObjectFactory getObjectFactory() {
        return objFact;
    }

    public Set<ConstraintViolation<Definitions>> validate(Definitions def)
            throws IOException {
        return de.getRepositoryService().validate(def);
    }

    public void writeDmn(Definitions decisionModel, String filename) {
        Writer out = null;
        try {
            File dmnFile = new File(outputDir, filename);
            System.out.println(String.format("Writing DMN to %1$s",
                    dmnFile.getAbsolutePath()));
            out = new FileWriter(dmnFile);
            getDecisionEngine().getRepositoryService()
                    .write(decisionModel, out);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

}
