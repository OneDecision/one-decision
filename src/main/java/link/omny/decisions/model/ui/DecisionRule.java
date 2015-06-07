package link.omny.decisions.model.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Entity
@Table(name = "OL_UI_RULE")
@Component
@NoArgsConstructor
public class DecisionRule {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionRule.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    // @NotNull
    // @JsonProperty
    // protected String name;

    @JsonProperty 
    protected String[] expressions;

    // @JsonProperty
    // protected String label;

    public DecisionRule(String[] expressions) {
        setExpressions(expressions);
    }

    // public DecisionRule(String name, String[] expressions) {
    // setName(name);
    // setExpressions(expressions);
    // }

    // public String getLabel() {
    // if (label == null) {
    // return name;
    // }
    // return label;
    // }
}
