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
package io.onedecision.engine.decisions.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class DataModelFactory {

    /**
     * 
     * @param resourceName
     * @return JSON string read from the resource.
     * @throws IOException
     *             If no such resource can be read.
     */
    public String load(String resourceName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourceName);
                Scanner s = new Scanner(is);) {
            String text = s.useDelimiter("\\A").next();
            return text;
        }
    }

}
