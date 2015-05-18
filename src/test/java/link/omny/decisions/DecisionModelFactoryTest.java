package link.omny.decisions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import link.omny.decisions.model.DecisionTableOrientation;
import link.omny.decisions.model.Definitions;
import link.omny.decisions.model.DrgElement;
import link.omny.decisions.model.Expression;
import link.omny.decisions.model.ObjectFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DecisionModelFactoryTest {

    private static final String DMN_RESOURCE = "/ApplicationRiskRating.dmn";
    // private static final String DECISION_ID = "DetermineApplicantRiskRating";

    private DecisionModelFactory fact;
    private Definitions dm;
    private Validator validator;

    @Before
    public void setUp() throws Exception {
        fact = new DecisionModelFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        dm = fact.load(DMN_RESOURCE);
    }

    @Test
    @Ignore
    // Not working possibly due to using Jackson 1 and anyway horribly verbose
    public void testXmlToJson() throws IOException {
        File dmnFile = new File("target", "ApplicationRiskRating.json");
        fact.write("application/json", dm, new FileWriter(dmnFile));
        assertTrue(dmnFile.exists());
    }

    @Test
    public void testSimpleSerialisation() {
        Definitions dm = new Definitions()
                .setId("dm0")
                .setName("Decision Model 1")
                .setDescription(
                        "A minimal test decision model to test the model serialisation and fluent API");

        Decision d = new Decision();
        assertTrue(d instanceof DrgElement);
        ObjectFactory of = new ObjectFactory();
        dm.getDrgElement().add(of.createDecision(d));

        DecisionTable dt = new DecisionTable().setName("Test 1")
                .setPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW);
        Clause c = new Clause();
        // Expression inputExpr1 = new LiteralExpression();
        // inputExpr1.setDescription("The first input expression");
        // c.getInputEntry().add(inputExpr1);
        dt.getClause().add(c);

        c = new Clause();
        // Expression outputExpr1 = new LiteralExpression();
        // outputExpr1.setDescription("output = 'bar'");
        // c.getOutputEntry().add(outputExpr1);
        dt.getClause().add(c);

        d.setExpression(of.createDecisionTable(dt));

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

        try {
            File f = new File("target", "Test-1.dmn");
            fact.write("application/dmn", dm, f);
            assertTrue("Unable to write DMN file", f.exists());

            f = new File("target", "Test-1b.json");
            fact.write("application/json", dm, f);
            assertTrue("Unable to write JSON file", f.exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

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
    public void testReadAndValidateApplicantRiskRating() {
        FileOutputStream fos = null;
        try {
            Definitions dm = fact.load("/ApplicationRiskRating.dmn");
            for (Decision d : dm.getDecisions()) {
                System.out.println("d: " + d.getId() + ":" + d.getName());
                
                DecisionTable dt = d.getDecisionTable();
                if (dt != null) {
                    List<Clause> clauses = dt.getClause();
                    for (Clause clause : clauses) {
                        System.out.println("clause: " + clause.getName());
                        for (Expression inputEntry : clause.getInputEntry()) {
                            System.out.println("in: " + inputEntry.getName());
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

            // Moxy (doesn't work)
            // Create a JaxBContext
            // JAXBContext jc = JAXBContext.newInstance(Definitions.class);
            // // Create the Marshaller Object using the JaxB Context
            // Marshaller marshaller = jc.createMarshaller();
            // // Set the Marshaller media type to JSON or XML
            // marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
            // "application/json");
            // // Set it to true if you need to include the JSON root element in
            // // the JSON output
            // marshaller
            // .setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
            // // Set it to true if you need the JSON output to formatted
            // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // fos = new FileOutputStream(new File("target", dm.getName()
            // + ".json"));
            // marshaller.marshal(dm, fos);

            File f = new File("target", dm.getName() + ".dmn");
            fact.write("application/dmn", dm, f);

            f = new File("target", dm.getName() + ".json");
            fact.write("application/json", dm, f);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }
}
