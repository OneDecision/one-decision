package org.omg.spec.dmn._20160719.testcase.json;

import java.io.IOException;

import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ValueSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen,
            SerializerProvider serializers) throws IOException,
            JsonProcessingException {
        if (value instanceof Element) {
            String content = ((Element) value).getTextContent();
            gen.writeString(content);
        }else {
            gen.writeObject(value);
        }
    }

}
