package link.omny.decisions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import link.omny.decisions.impl.DecisionTableFactory;
import link.omny.decisions.model.Clause;
import link.omny.decisions.model.Decision;
import link.omny.decisions.model.DecisionTable;
import link.omny.decisions.model.DecisionTableOrientation;
import link.omny.decisions.model.Definitions;
import link.omny.decisions.model.DrgElement;
import link.omny.decisions.model.Expression;
import link.omny.decisions.model.LiteralExpression;

import org.junit.Before;
import org.junit.Test;

public class DecisionTableBuilderTest {

    private DecisionTableFactory fact;
    private Validator validator;

    @Before
    public void setUp() {
        fact = new DecisionTableFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSimpleSerialisation() {
        Definitions dm = new Definitions();

        Decision d = new Decision();
        assertTrue(d instanceof DrgElement);
        dm.getDrgElement().add(d);

        DecisionTable dt = new DecisionTable().setName("Test 1")
                .setPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW);
        Clause c = new Clause();
        Expression inputExpr1 = new LiteralExpression();
        inputExpr1.setDescription("input expr 1 == 'foo'");
        c.getInputEntry().add(inputExpr1);
        dt.getClause().add(c);

        c = new Clause();
        Expression outputExpr1 = new LiteralExpression();
        outputExpr1.setDescription("output = 'bar'");
        c.getOutputEntry().add(outputExpr1);

        dt.getClause().add(c);

        d.setExpression(dt);
        // ElementCollection ec = new ElementCollection();
        // ec.getDrgElement().add(dt);
        // dm.getElementCollection().add(ec);

        Set<ConstraintViolation<Definitions>> constraintViolations = validator
                .validate(dm);
        for (ConstraintViolation<Definitions> violation : constraintViolations) {
            System.out.println("  "
                    + violation.getLeafBean().getClass().getName() + "."
                    + violation.getPropertyPath() + " "
                    + violation.getMessage());
        }
        assertEquals("Decision Table is not valid", 0,
                constraintViolations.size());

        File f = new File("target", "Test-1.dmn");
        try {
            fact.write(dm, f);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testApplicantRiskRating() {
        Definitions dm = new Definitions();

        Decision d = new Decision();
        assertTrue(d instanceof DrgElement);
        dm.getDrgElement().add(d);

        String name = "Applicant Risk Rating";
        DecisionTable dt = new DecisionTable().setName(name)
                .setPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW);

        LiteralExpression inputExpr1 = new LiteralExpression()
.setDescription(
                "Under 60").setText(new LiteralExpression.Text());
        dt.getClause().add(new Clause().addInputEntry(inputExpr1));

        Expression outputExpr1 = new LiteralExpression()
                .setDescription("output = 'bar'");
        dt.getClause().add(new Clause().addOutputEntry(outputExpr1));

        d.setExpression(dt);

        Set<ConstraintViolation<Definitions>> constraintViolations = validator
                .validate(dm);
        for (ConstraintViolation<Definitions> violation : constraintViolations) {
            System.out.println("  "
                    + violation.getLeafBean().getClass().getName() + "."
                    + violation.getPropertyPath() + " "
                    + violation.getMessage());
        }
        assertEquals("Decision Table is not valid", 0,
                constraintViolations.size());

        File f = new File("target", name + ".dmn");
        try {
            fact.write(dm, f);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
