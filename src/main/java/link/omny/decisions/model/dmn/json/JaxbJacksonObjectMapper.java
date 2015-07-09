package link.omny.decisions.model.dmn.json;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;


public class JaxbJacksonObjectMapper extends ObjectMapper {

    public JaxbJacksonObjectMapper() {
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        super.getDeserializationConfig()
.withAppendedAnnotationIntrospector(
                introspector);
        super.getSerializationConfig().withAppendedAnnotationIntrospector(
                introspector);
    }

}