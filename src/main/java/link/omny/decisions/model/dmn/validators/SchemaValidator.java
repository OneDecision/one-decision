package link.omny.decisions.model.dmn.validators;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.springframework.validation.Errors;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaValidator implements ErrorHandler {

    class LocalResourceResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI,
                String publicId, String systemId, String baseURI) {
            DOMImplementationRegistry registry;
            try {
                registry = DOMImplementationRegistry.newInstance();
            } catch (Exception e) {
                return null;
            }
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
                    .getDOMImplementation("LS");
            LSInput ret = domImplementationLS.createLSInput();

            InputStream is = null;
            try {
                is = getRessourceAsStreamWrapper("schema/" + systemId);
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            ret.setSystemId(systemId);
            ret.setByteStream(is);
            return ret;
        }
    }

    private List<Exception> errors = new ArrayList<Exception>();

    private InputStream getRessourceAsStreamWrapper(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        if (is == null)
            is = getClass().getResourceAsStream("/" + name);
        return is;
    }

	public void validate(InputStream obj, Errors errors) {
        InputStream is = (InputStream) obj;

        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new LocalResourceResolver());

        try {
            schemaFactory.newSchema(new StreamSource(
                    getRessourceAsStreamWrapper("schema/DMN10.xsd")));
        } catch (SAXException e1) {
            errors.reject("Cannot find / read schema",
                    "Exception: " + e1.getMessage());
        }

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        // parserFactory.setValidating(true);
        parserFactory.setNamespaceAware(true);
        // parserFactory.setSchema(schema);

        SAXParser parser = null;
        try {
            parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(this);
        } catch (ParserConfigurationException e1) {
            errors.reject("Cannot create parser",
                    "Exception: " + e1.getMessage());
        } catch (SAXException e1) {
            errors.reject("Parser cannot be created",
                    "Exception: " + e1.getMessage());
        }

        try {
            parser.parse(new InputSource(is), (DefaultHandler) null);
        } catch (Exception e) {
            errors.reject("Schema validation failed",
                    "Exception: " + e.getMessage());
            // e.printStackTrace(System.out);
        }
        if (this.errors.size() > 0) {
            errors.reject("Schema validation failed", this.errors.toString());
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        errors.add(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        errors.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        errors.add(exception);
    }
}
