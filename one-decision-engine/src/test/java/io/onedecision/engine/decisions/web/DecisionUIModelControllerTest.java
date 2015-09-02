package io.onedecision.engine.decisions.web;

import io.onedecision.engine.Application;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DecisionUIModelControllerTest {

    @Autowired
    private DecisionUIModelController controller;

    @Test
    @Ignore
    // TODO currently failing see DecisionExpression.expressions
    public void testInstall() {
        controller.installExamples("onedecision");
    }

}
