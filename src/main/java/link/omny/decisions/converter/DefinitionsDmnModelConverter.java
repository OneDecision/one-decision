package link.omny.decisions.converter;

import java.io.IOException;
import java.io.StringWriter;

import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.model.dmn.Definitions;
import link.omny.decisions.model.dmn.DmnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts between the UI model and DMN serialisation.
 * 
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
