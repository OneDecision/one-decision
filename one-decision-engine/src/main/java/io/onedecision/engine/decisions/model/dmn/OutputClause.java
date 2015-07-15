package io.onedecision.engine.decisions.model.dmn;

import java.util.List;

import javax.xml.namespace.QName;

public class OutputClause extends Clause {

    public OutputClause(String localName, List<Expression> entries) {
        setOutputDefinition(new QName(localName));
        getOutputEntry().addAll(entries);
    }

    public OutputClause(String localName) {
        setOutputDefinition(new QName(localName));
    }
    
    public OutputClause() {}

}
