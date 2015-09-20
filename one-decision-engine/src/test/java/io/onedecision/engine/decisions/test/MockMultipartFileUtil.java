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
package io.onedecision.engine.decisions.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class MockMultipartFileUtil {

    public static MultipartFile newInstance(String dmnResource) {
        File baseDir = new File("target" + File.separator + "test-classes");
        File dmnToUpload = new File(baseDir, dmnResource);
        assertTrue("Cannot find DMN file to use as test input",
                dmnToUpload.exists());

        Path path = Paths.get(dmnToUpload.getAbsolutePath());
        String name = "file.dmn";
        String originalFileName = "file.dmn";
        String contentType = "application/xml";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile mpf = new MockMultipartFile(name, originalFileName,
                contentType, content);
        return mpf;
    }

}
