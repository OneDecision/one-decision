package io.onedecision.engine.decisions.model.dmn.validators;

import io.onedecision.engine.decisions.model.dmn.BusinessKnowledgeModel;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ValidationTest {

    private static ObjectFactory objFact;

    private static Validator validator;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        objFact = new ObjectFactory();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void test() {
        User user = new User();
        user.setWorking(true);
        user.setAboutMe("Its all about me!");
        user.setAge(50);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            System.out.println("X " + violation.getMessage());
        }

    }

    @Test
    @Ignore
    public void testInvalidConnectionFromDecisionToBkm() {
        Definitions definitions = objFact.createDefinitions();

        BusinessKnowledgeModel bkm = objFact.createBusinessKnowledgeModel();
        Decision decision = objFact.createDecision();
        decision.getKnowledgeRequirements()
                .add(objFact.createKnowledgeRequirement()
                        .withRequiredKnowledge(bkm));

        Set<ConstraintViolation<Definitions>> violations = validator
                .validate(definitions);
        for (ConstraintViolation<Definitions> violation : violations) {
            System.out.println("X " + violation.getMessage());
        }
    }

}
