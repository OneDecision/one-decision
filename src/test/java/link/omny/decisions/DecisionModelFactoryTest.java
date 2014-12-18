package link.omny.decisions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.model.Clause;
import link.omny.decisions.model.Decision;
import link.omny.decisions.model.DecisionRule;
import link.omny.decisions.model.DecisionTable;
import link.omny.decisions.model.Definitions;
import link.omny.decisions.model.Expression;

import org.junit.Before;
import org.junit.Test;

public class DecisionModelFactoryTest {

    private DecisionModelFactory fact;
    private Validator validator;

    @Before
    public void setUp() {
        fact = new DecisionModelFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // @Test
    // public void testSimpleSerialisation() {
    // Definitions dm = new Definitions();
    //
    // Decision d = new Decision();
    // assertTrue(d instanceof DrgElement);
    // dm.getDrgElement().add(d);
    //
    // DecisionTable dt = new DecisionTable().setName("Test 1")
    // .setPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW);
    // Clause c = new Clause();
    // Expression inputExpr1 = new LiteralExpression();
    // inputExpr1.setDescription("input expr 1 == 'foo'");
    // c.getInputEntry().add(inputExpr1);
    // dt.getClause().add(c);
    //
    // c = new Clause();
    // Expression outputExpr1 = new LiteralExpression();
    // outputExpr1.setDescription("output = 'bar'");
    // c.getOutputEntry().add(outputExpr1);
    //
    // dt.getClause().add(c);
    //
    // d.setExpression(dt);
    // // ElementCollection ec = new ElementCollection();
    // // ec.getDrgElement().add(dt);
    // // dm.getElementCollection().add(ec);
    //
    // Set<ConstraintViolation<Definitions>> constraintViolations = validator
    // .validate(dm);
    // for (ConstraintViolation<Definitions> violation : constraintViolations) {
    // System.out.println("  "
    // + violation.getLeafBean().getClass().getName() + "."
    // + violation.getPropertyPath() + " "
    // + violation.getMessage());
    // }
    // assertEquals("Decision Table is not valid", 0,
    // constraintViolations.size());
    //
    // File f = new File("target", "Test-1.dmn");
    // try {
    // fact.write(dm, f);
    // } catch (Exception e) {
    // e.printStackTrace();
    // fail(e.getMessage());
    // }
    //
    // }
    //
    // @Test
    // public void testApplicantRiskRating() {
    // Definitions dm = new Definitions();
    //
    // Decision d = new Decision();
    // assertTrue(d instanceof DrgElement);
    // dm.getDrgElement().add(d);
    //
    // String name = "Applicant Risk Rating";
    // DecisionTable dt = new DecisionTable().setName(name)
    // .setPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW);
    //
    // LiteralExpression inputExpr1 = new LiteralExpression()
    // .setDescription(
    // "Under 60").setText(new LiteralExpression.Text());
    // dt.getClause().add(new Clause().addInputEntry(inputExpr1));
    //
    // Expression outputExpr1 = new LiteralExpression()
    // .setDescription("output = 'bar'");
    // dt.getClause().add(new Clause().addOutputEntry(outputExpr1));
    //
    // d.setExpression(dt);
    //
    // Set<ConstraintViolation<Definitions>> constraintViolations = validator
    // .validate(dm);
    // for (ConstraintViolation<Definitions> violation : constraintViolations) {
    // System.out.println("  "
    // + violation.getLeafBean().getClass().getName() + "."
    // + violation.getPropertyPath() + " "
    // + violation.getMessage());
    // }
    // assertEquals("Decision Table is not valid", 0,
    // constraintViolations.size());
    //
    // File f = new File("target", name + ".dmn");
    // try {
    // fact.write(dm, f);
    // } catch (Exception e) {
    // e.printStackTrace();
    // fail(e.getMessage());
    // }
    //
    // }

    @Test
    public void testApplicantRiskRatingDeserialise() {
        try {
            Definitions dm = fact.load("/ApplicationRiskRating.dmn");
            for (Decision d : dm.getDecisions()) {
                System.out.println("d: " + d.getId() + ":" + d.getName());
                
                // d.get
                
                DecisionTable dt = d.getDecisionTable();
                if (dt != null) {
                    List<Clause> clauses = dt.getClause();
                    for (Clause clause : clauses) {
                        System.out.println("clause: " + clause.getName());
                        for (Expression inputEntry : clause.getInputEntry()) {
                            System.out.println("in: " + inputEntry.getName());
                            // System.out
                            // .println("  var: "
                            // + inputEntry.getInputVariable() == null ? null
                            // : inputEntry.getInputVariable()
                            // .get(0).getValue());

                            // System.out.println("  any: " +
                            // inputEntry.getAny());
                            // for (Object o : inputEntry.getAny()) {
                            // System.out.println("    class:"
                            // + o.getClass().getName());
                            // if (o instanceof Element) {
                            // System.out.println("      val: "
                            // + ((Element) o).getTextContent());
                            // }
                            // }

                            // System.out.println("  class: "
                            // + inputEntry.getClass().getName());

                            // Expressions appear to never be read as sub-class
                            // but always as Expression
                            // if (inputEntry instanceof LiteralExpression) {
                            // System.out.println("    text:"
                            // + ((LiteralExpression) inputEntry)
                            // .getText());
                            // }
                        }
                        List<DecisionRule> rules = dt.getRule();
                        for (DecisionRule rule : rules) {
                            System.out.println("rule: " + rule);
                            System.out.println("rule condition: "
                                    + rule.getConditions());
                            System.out.println("rule conclusion: "
                                    + rule.getConclusions());
                        }
                    }
                }
            }

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

            File f = new File("target", dm.getName() + ".dmn");
            fact.write(dm, f);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
