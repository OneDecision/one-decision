package link.omny.decisions.model.ui;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "OL_UI_MODEL")
@Component
@NoArgsConstructor
public class DecisionModel implements Serializable {

    private static final long serialVersionUID = -1955316879920138892L;

    // protected static final Logger LOGGER = LoggerFactory
    // .getLogger(DecisionModel2.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    protected String name;

    @JsonProperty
    protected String hitPolicy;

    @JsonProperty
    protected String domainModelUri;

    @Embedded
    @JsonProperty
    private List<String> inputs;

    @Embedded
    @JsonProperty
    private List<String> outputs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DecisionExpression> conditions;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DecisionExpression> conclusions;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DecisionExpression> rules;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonProperty
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    // @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonProperty
    private Date lastUpdated;

    @NotNull
    @JsonProperty
    @Column(nullable = false)
    private String tenantId;

    @PrePersist
    public void preInsert() {
        created = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        // if (LOGGER.isWarnEnabled() && lastUpdated != null) {
        // LOGGER.warn(String.format(
        // "Overwriting update date %1$s with 'now'.", lastUpdated));
        // }
        lastUpdated = new Date();
    }
}
