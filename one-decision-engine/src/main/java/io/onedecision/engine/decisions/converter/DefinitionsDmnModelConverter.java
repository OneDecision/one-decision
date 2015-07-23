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
package io.onedecision.engine.decisions.converter;

import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.api.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DefinitionsDmnModelConverter.class);

    @Autowired
    private DecisionModelFactory decisionModelFactory;

    @Override
    public DmnModel convert(Definitions model) {

        StringWriter sw = new StringWriter();
        try {
            decisionModelFactory.write("application/xml", model, sw);
        } catch (IOException e) {
            String msg = "Unable to serialize definitions model";
            LOGGER.error(msg, e);
            throw new DecisionException(msg, e);
        }
        return new DmnModel(model, sw.toString());
    }

}
