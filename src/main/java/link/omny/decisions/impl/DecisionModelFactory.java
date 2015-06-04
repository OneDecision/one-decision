package link.omny.decisions.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import link.omny.decisions.model.dmn.Decision;
import link.omny.decisions.model.dmn.Definitions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@Component
public class DecisionModelFactory {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionModelFactory.class);

    private com.fasterxml.jackson.databind.ObjectMapper mapper;

    public DecisionModelFactory() {
    }

    public void write(String mediaType, Definitions dm, File f)
            throws IOException {
        FileWriter out = new FileWriter(f);
        switch (mediaType) {
        case "application/json":
            writeAsJson(dm, out);
            break;
        default:
            writeAsXml(dm, out);
        }
    }

    public void write(String mediaType, Definitions dm, Writer out)
            throws IOException {
        switch (mediaType) {
        case "application/json":
            writeAsJson(dm, out);
            break;
        default:
            writeAsXml(dm, out);
        }
    }

    public void write(String mediaType, List<Decision> list, Writer out)
            throws IOException {
        switch (mediaType) {
        case "application/json":
            getObjectMapper().writeValue(out, list);
            break;
        default:
            writeAsXml(list, out);
        }
    }

    private void writeAsJson(Definitions dm, Writer writer) throws IOException {
        getObjectMapper().writeValue(writer, dm);
    }

    private ObjectMapper getObjectMapper() throws IOException {
        if (mapper == null) {
            mapper = new ObjectMapper();
            AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
            // make deserializer use JAXB annotations (only)
            mapper.getDeserializationConfig()
                    .withAppendedAnnotationIntrospector(
                    introspector);
            // make serializer use JAXB annotations (only)
            mapper.getSerializationConfig().withAppendedAnnotationIntrospector(
                    introspector);
            // TODO omit null values, but not like this
            // StdSerializerProvider provider = new StdSerializerProvider();
            // provider.setNullValueSerializer(new NoOpNullSerializer());
            // mapper.setSerializerProvider(provider);
        }
        return mapper;
    }

    private void writeAsXml(Definitions dm, Writer writer) throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Marshaller m = context.createMarshaller();
            m.marshal(dm, writer);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private void writeAsXml(List<Decision> list, Writer writer)
            throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Decision.class);
            Marshaller m = context.createMarshaller();
            m.marshal(list, writer);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
    
    public Definitions load(String resourceName) throws IOException {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(resourceName);
            return load(is);
        } catch (IOException e) {
            String msg = "Unable to load decision model from " + resourceName;
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    public Definitions load(InputStream inputStream) throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Unmarshaller um = context.createUnmarshaller();

            Object dm = um.unmarshal(inputStream);
            if (dm instanceof JAXBElement<?>) {
                return ((JAXBElement<Definitions>) dm).getValue();
            } else {
                return (Definitions) dm;
            }
        } catch (JAXBException e) {
            String msg = "Unable to load decision model from stream";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        }
    }

    /**
     * Search for the requested decision.
     * 
     * For now this is simply a resource check, but will add a more full-fledged
     * database backend in due course.
     * 
     * @param definitionsId
     *            The id of the decision model (definitions element id).
     * @return decision model.
     * @throws IOException
     *             If the decision model cannot be found.
     */
    public Definitions find(String definitionsId) throws IOException {
        return load("/" + definitionsId + ".dmn");
    }

    /**
     * Search for the requested decision.
     * 
     * For now this is simply a resource check, but will add a more full-fledged
     * database backend in due course.
     * 
     * @param definitionsId
     *            The id of the decision model (definitions element id).
     * @param decisionId
     *            The id of the decision sought (within the decision model)
     * @return
     * @throws IOException
     *             If the decision model or decision cannot be found.
     */
    public Decision find(String definitionsId, String decisionId)
            throws IOException {
        return load("/" + definitionsId + ".dmn").getDecisionById(decisionId);
    }
}
