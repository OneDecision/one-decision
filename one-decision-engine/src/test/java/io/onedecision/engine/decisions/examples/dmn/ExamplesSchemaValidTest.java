/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;

import java.io.InputStream;
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
    public static void setUpClass() throws Exception {
		schemaValidator = new SchemaValidator();
	}

	@Parameters
	public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                { ExamplesConstants.ARR_DMN_RESOURCE },
                { ExamplesConstants.CD_DMN_RESOURCE },
                { ExamplesConstants.EFU_DMN_RESOURCE },
                { ExamplesConstants.FIG27_DMN_RESOURCE } });
	}

	private String resourceName;

	public ExamplesSchemaValidTest(String resourceName) {
		this.resourceName = resourceName;
	}

	@Test
	public void testSchemaValid() {
        DmnValidationErrors errors = new DmnValidationErrors(resourceName);
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(resourceName);
            assertNotNull(String.format(
                    "Unable to find resource %1$s to validate", resourceName),
                    is);
            schemaValidator.validate(is, errors);
            assertTrue(!errors.hasErrors());
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
	}

}
