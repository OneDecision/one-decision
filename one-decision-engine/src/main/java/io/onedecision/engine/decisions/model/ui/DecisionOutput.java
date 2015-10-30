package io.onedecision.engine.decisions.model.ui;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Decision Rule allowed output
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "OL_UI_OUTPUT")
public class DecisionOutput extends DecisionExpression {

    private static final long serialVersionUID = -6088512588180027199L;

    public DecisionOutput withName(String name) {
        setName(name);
        return this;
    }

    public DecisionOutput withLabel(String label) {
        setLabel(label);
        return this;
    }

}
