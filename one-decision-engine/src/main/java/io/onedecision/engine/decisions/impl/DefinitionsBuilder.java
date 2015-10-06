package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;

public class DefinitionsBuilder {

    private static ObjectFactory objFact;
    private Definitions definitions;

    public DefinitionsBuilder() {
        this.definitions = objFact.createDefinitions();
    }

    public Definitions build() {
        return definitions;
    }

    public DefinitionsBuilder setId(String id) {
        definitions.setId(id);
        return this;
    }



}
