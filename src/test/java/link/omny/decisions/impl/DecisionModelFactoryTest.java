package link.omny.decisions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.namespace.QName;

import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.model.dmn.Clause;
import link.omny.decisions.model.dmn.Decision;
import link.omny.decisions.model.dmn.DecisionRule;
import link.omny.decisions.model.dmn.DecisionTable;
import link.omny.decisions.model.dmn.DecisionTableOrientation;
import link.omny.decisions.model.dmn.Definitions;
import link.omny.decisions.model.dmn.DrgElement;
import link.omny.decisions.model.dmn.Expression;
import link.omny.decisions.model.dmn.InformationItem;
import link.omny.decisions.model.dmn.LiteralExpression;
import link.omny.decisions.model.dmn.ObjectFactory;

import org.junit.Before;
import org.junit.Test;

public class DecisionModelFactoryTest implements ExamplesConstants {

    private DecisionModelFactory fact;
    private Validator validator;

    @Before
    public void setUp() throws Exception {
        fact = new DecisionModelFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSimpleSerialisation() throws IOException {
        Definitions dm = new Definitions()
                .setId("dm0")
                .setName("Decision Model 1")
                .setDescription(
                        "A minimal test decision model to test the model serialisation API");

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

        File f = new File("target", "Test-1.dmn");
        fact.write("application/dmn", dm, f);
        assertTrue("Unable to write DMN file", f.exists());
    }

    @Test
    public void testReadAndValidateApplicantRiskRating() throws IOException {
        Definitions dm = fact.loadFromClassPath(ARR_DMN_RESOURCE);

        assertNotNull("Definitions element was null", dm);
        assertEquals(2, dm.getItemDefinition().size());
        assertEquals(1, dm.getDecisions().size());
        assertEquals("Applicant Model", dm.getBusinessKnowledgeModel()
                .getName());
        assertEquals(1, dm.getBusinessKnowledgeModel().getInformationItem()
                .size());
        assertEquals("applicant", dm.getBusinessKnowledgeModel()
                .getInformationItem().get(0).getId());
        // TODO What about this:
        // <LiteralExpression expressionLanguage="http://tempuri.org"
        // id="idvalue2" name="conclusion">...

        for (Decision d : dm.getDecisions()) {
            System.out.println("d: " + d.getId() + ":" + d.getName());
            assertEquals("DetermineApplicantRiskRating", d.getId());

            DecisionTable dt = d.getDecisionTable();
            assertEquals("dt0", dt.getId());
            if (dt != null) {
                List<Clause> clauses = dt.getClause();
                assertEquals(3, clauses.size());
                for (int i = 0; i < 3; i++) {
                    Clause clause = clauses.get(i);
                    switch (i) {
                    case 0:
                        Expression inExpr = clause.getInputExpression();
                        assertNotNull(inExpr);
                        assertEquals("dt0_c0_ie", inExpr.getId());
                        assertEquals(1, inExpr.getInputVariable().size());
                        assertEquals("applicant", ((InformationItem) inExpr
                                .getInputVariable().get(0).getValue()).getId());
                        assertEquals("applicant", inExpr.getOnlyInputVariable()
                                .getId());
                        assertEquals(3, clause.getInputEntry().size());
                        for (int j = 0; j < 3; j++) {
                            Expression inEntry = clause.getInputEntry().get(j);
                            switch (j) {
                            case 0:
                                assertTrue(inEntry instanceof LiteralExpression);
                                LiteralExpression le = (LiteralExpression) inEntry;
                                assertEquals("applicant.age > 60",
                                        le.getText().getContent().get(0));
                                break;
                            }
                        }
                        break;
                    case 1:
                        inExpr = clause.getInputExpression();
                        assertNotNull(inExpr);
                        assertEquals("dt0_c1_ie", inExpr.getId());
                        assertEquals(1, inExpr.getInputVariable().size());
                        assertEquals("applicant", ((InformationItem) inExpr
                                .getInputVariable().get(0).getValue()).getId());
                        assertEquals("applicant", inExpr.getOnlyInputVariable()
                                .getId());
                        break;
                    case 2:
                        QName outDef = clause.getOutputDefinition();
                        assertNotNull(outDef);
                        assertEquals("conclusion", outDef.getLocalPart());
                        assertEquals(3, clause.getOutputEntry().size());

                        break;
                    }
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

        File f = new File("target", dm.getName() + ".dmn");
        fact.write("application/dmn", dm, f);
        assertTrue(f.exists());
        System.out.println("wrote dmn to :" + f.getAbsolutePath());
    }
}
