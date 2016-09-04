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
package io.onedecision.engine.decisions.model.dmn.validators;

import io.onedecision.engine.decisions.model.dmn.Definitions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaValidator implements ErrorHandler {

    public static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SchemaValidator.class);

    private List<Exception> errors = new ArrayList<Exception>();

    public Set<ConstraintViolation<Definitions>> validate(InputStream obj) {
        HashSet<ConstraintViolation<Definitions>> violations = new HashSet<ConstraintViolation<Definitions>>();

        try {
            validateSchema(obj);
            for (Exception ex : errors) {
                violations.add(new SchemaConstraintViolation(ex));
            }
        } catch (ParserConfigurationException e) {
            violations.add(new SchemaConstraintViolation(e));
        } catch (SAXException e) {
            violations.add(new SchemaConstraintViolation(e));
        } catch (IOException e) {
            violations.add(new SchemaConstraintViolation(e));
        }

        return violations;
    }

    public void validate(InputStream obj, Errors errors) {
        try {
            validateSchema(obj);

            if (this.errors.size() > 0) {
                errors.reject("Schema validation failed",
                        this.errors.toString());
            }
        } catch (ParserConfigurationException e1) {
            errors.reject("Cannot create parser",
                    "Exception: " + e1.getMessage());
        } catch (SAXException e1) {
            errors.reject("Parser cannot be created",
                    "Exception: " + e1.getMessage());
        } catch (Exception e) {
            String msg = "Schema validation failed";
            LOGGER.error(msg, e);
            errors.reject(msg, "Exception: " + e.getMessage());
        }
    }

    protected void validateSchema(InputStream obj)
            throws ParserConfigurationException, SAXException, IOException {
        InputStream is = (InputStream) obj;

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(true);
        parserFactory.setNamespaceAware(true);

        SAXParser parser = null;
        parser = parserFactory.newSAXParser();

        parser.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
        InputStream schema = getClass().getResourceAsStream("/schema/dmn.xsd");
        parser.setProperty(SCHEMA_SOURCE, schema);

        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(this);

        parser.parse(new InputSource(is), (DefaultHandler) null);
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
