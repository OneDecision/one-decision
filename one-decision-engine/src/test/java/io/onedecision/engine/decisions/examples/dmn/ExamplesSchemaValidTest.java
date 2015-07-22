package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * At a minimum this test ensures all examples are schema valid.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class ExamplesSchemaValidTest {

	private static SchemaValidator schemaValidator;

	@BeforeClass
	public static void setUpClasss() throws Exception {
		schemaValidator = new SchemaValidator();
	}

	@Parameters
	public static Collection<String[]> data() {
		return Arrays
				.asList(new String[][] { { ExamplesConstants.ARR_DMN_RESOURCE } });
	}

	private String resourceName;

	public ExamplesSchemaValidTest(String resourceName) {
		this.resourceName = resourceName;
	}

	@Test
	public void testSchemaValid() {
		DmnValidationErrors errors = new DmnValidationErrors();
		schemaValidator.validate(getClass().getResourceAsStream(resourceName),
				errors);
		assertTrue(!errors.hasErrors());
	}

}
