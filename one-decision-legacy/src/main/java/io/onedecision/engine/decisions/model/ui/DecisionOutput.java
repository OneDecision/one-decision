package io.onedecision.engine.decisions.model.ui;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @deprecated Since 1.2 focus on DMN models as there are now plenty of modeling
 *             tools.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "OL_UI_OUTPUT")
public class DecisionOutput extends DecisionExpression {

    private static final long serialVersionUID = -6088512588180027199L;

    public void setAllowedValues(String... unaryTests) {
        setExpressions(unaryTests);
    }

    public DecisionOutput withAllowedValues(String... unaryTests) {
        setExpressions(unaryTests);
        return this;
    }

    public DecisionOutput withName(String name) {
        setName(name);
        return this;
    }

    public DecisionOutput withLabel(String label) {
        setLabel(label);
        return this;
    }
    
    

}
