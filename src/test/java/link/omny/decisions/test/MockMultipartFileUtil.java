package link.omny.decisions.test;

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
