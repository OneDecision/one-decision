package io.onedecision.engine.decisions.model.ui;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "OL_UI_EXPRESSION")
@Component
public class DecisionExpression {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DecisionExpression.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    protected String name;

    @JsonProperty 
    protected String[] expressions;

    @JsonProperty
    protected String label;

    public DecisionExpression() {}
    
    public DecisionExpression(List<String> expressions) {
        setExpressions((String[]) expressions.toArray());
    }

    public DecisionExpression(String name, String[] expressions) {
        setName(name);
        setExpressions(expressions);
    }

    public String getLabel() {
        if (label == null) {
            return name;
        }
        return label;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getExpressions() {
		return expressions;
	}

	public void setExpressions(String[] expressions) {
		this.expressions = expressions;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
