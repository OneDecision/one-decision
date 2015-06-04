package link.omny.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@Component
@NoArgsConstructor
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
}
