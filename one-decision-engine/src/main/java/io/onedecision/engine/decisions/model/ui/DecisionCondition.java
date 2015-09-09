package io.onedecision.engine.decisions.model.ui;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "OL_UI_CONDITION")
public class DecisionCondition extends DecisionExpression {

    private static final long serialVersionUID = 8264530897908737726L;

    public DecisionCondition() {
        super();
    }

    public DecisionCondition(String name, String[] expressions) {
        this();
        setName(name);
        setExpressions(expressions);
    }

    public DecisionCondition(String name, String label, String[] expressions) {
        this();
        setName(name);
        setLabel(label);
        setExpressions(expressions);
    }

}
