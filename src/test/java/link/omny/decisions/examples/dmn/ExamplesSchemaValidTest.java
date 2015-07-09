package link.omny.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.model.dmn.validators.DmnErrors;
import link.omny.decisions.model.dmn.validators.SchemaValidator;

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
		return Arrays.asList(new String[][] {
				{ ExamplesConstants.ARR_DMN_RESOURCE },
				{ ExamplesConstants.EFU_DMN_RESOURCE } });
	}

	private String resourceName;

	public ExamplesSchemaValidTest(String resourceName) {
		this.resourceName = resourceName;
	}

	@Test
	public void testSchemaValid() {
		DmnErrors errors = new DmnErrors();
		schemaValidator.validate(getClass().getResourceAsStream(resourceName),
				errors);
		assertTrue(!errors.hasErrors());
	}

}
