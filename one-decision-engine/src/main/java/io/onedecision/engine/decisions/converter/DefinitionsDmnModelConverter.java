package io.onedecision.engine.decisions.converter;

import io.onedecision.engine.decisions.api.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts between the DMN Definitions object and the persistent wrapper class.
 * 
 * @see Definitions
 * @see DmnModel
 * @author Tim Stephenson
 */
@Component
public class DefinitionsDmnModelConverter implements
        Converter<Definitions, DmnModel> {

    @Autowired
    private DecisionModelFactory decisionModelFactory;

    @Override
    public DmnModel convert(Definitions model) {

        StringWriter sw = new StringWriter();
        try {
            decisionModelFactory.write("application/xml", model, sw);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new DmnModel(model, sw.toString());
    }

}
