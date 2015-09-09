package io.onedecision.engine.decisions.model.ui;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "OL_UI_CONCLUSION")
public class DecisionConclusion extends DecisionExpression {

    private static final long serialVersionUID = 3421337301816728913L;

    public DecisionConclusion() {
        super();
    }

    public DecisionConclusion(String name, String[] expressions) {
        this();
        setName(name);
        setExpressions(expressions);
    }

    public DecisionConclusion(String name, String label, String[] expressions) {
        this();
        setName(name);
        setLabel(label);
        setExpressions(expressions);
    }

}
