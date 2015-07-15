package io.onedecision.engine.decisions.model.ui;

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

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "OL_UI_MODEL")
@Component
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

	public String getHitPolicy() {
		return hitPolicy;
	}

	public void setHitPolicy(String hitPolicy) {
		this.hitPolicy = hitPolicy;
	}

	public String getDomainModelUri() {
		return domainModelUri;
	}

	public void setDomainModelUri(String domainModelUri) {
		this.domainModelUri = domainModelUri;
	}

	public List<String> getInputs() {
		return inputs;
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	public List<DecisionExpression> getConditions() {
		return conditions;
	}

	public void setConditions(List<DecisionExpression> conditions) {
		this.conditions = conditions;
	}

	public List<DecisionExpression> getConclusions() {
		return conclusions;
	}

	public void setConclusions(List<DecisionExpression> conclusions) {
		this.conclusions = conclusions;
	}

	public List<DecisionExpression> getRules() {
		return rules;
	}

	public void setRules(List<DecisionExpression> rules) {
		this.rules = rules;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conclusions == null) ? 0 : conclusions.hashCode());
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result
				+ ((domainModelUri == null) ? 0 : domainModelUri.hashCode());
		result = prime * result
				+ ((hitPolicy == null) ? 0 : hitPolicy.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
		result = prime * result
				+ ((tenantId == null) ? 0 : tenantId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DecisionModel other = (DecisionModel) obj;
		if (conclusions == null) {
			if (other.conclusions != null)
				return false;
		} else if (!conclusions.equals(other.conclusions))
			return false;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (domainModelUri == null) {
			if (other.domainModelUri != null)
				return false;
		} else if (!domainModelUri.equals(other.domainModelUri))
			return false;
		if (hitPolicy == null) {
			if (other.hitPolicy != null)
				return false;
		} else if (!hitPolicy.equals(other.hitPolicy))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		return true;
	}
}
