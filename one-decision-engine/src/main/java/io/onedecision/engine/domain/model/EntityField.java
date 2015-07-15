package io.onedecision.engine.domain.model;

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
@Table(name = "OL_DOMAIN_FIELD")
@Component
public class EntityField {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(EntityField.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    protected String name;

    @JsonProperty
    protected String label;

    @JsonProperty
    protected String hint;

    @JsonProperty
    protected boolean required = false;

    @JsonProperty
    protected String type;

    @JsonProperty
    protected String validation;

    @JsonProperty
    protected boolean builtIn = true;
    
    public EntityField() {}

    public EntityField(String name, String label, String hint,
            boolean required, String type) {
        setName(name);
        setLabel(label);
        setHint(hint);
        setRequired(required);
        setType(type);
    }

    public EntityField(String name, String label, String hint,
            boolean required, String type, String validation) {
        setName(name);
        setLabel(label);
        setHint(hint);
        setRequired(required);
        setType(type);
        setValidation(validation);
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public boolean isBuiltIn() {
		return builtIn;
	}

	public void setBuiltIn(boolean builtIn) {
		this.builtIn = builtIn;
	}
}
