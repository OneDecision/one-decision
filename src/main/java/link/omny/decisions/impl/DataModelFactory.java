package link.omny.decisions.impl;

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
