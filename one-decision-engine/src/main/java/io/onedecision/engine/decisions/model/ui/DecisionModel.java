/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.decisions.model.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

    private static final long serialVersionUID = -3986716944265142506L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    protected String decisionId;

    @JsonProperty
    protected String name;

    @JsonProperty
    protected String hitPolicy;

    @JsonProperty
    protected String domainModelUri;

	public DecisionModel() {
		created = new Date();
	}

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
     @JoinColumn(name = "MODEL_ID")
    private List<DecisionInput> inputs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
     @JoinColumn(name = "MODEL_ID")
    private List<DecisionOutput> outputs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "MODEL_ID")
    private List<DecisionRule> rules;

    @Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonProperty
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date lastUpdated;

    @NotNull
    @JsonProperty
    @Column(nullable = false)
    private String tenantId;

    @PrePersist
    public void preInsert() {
		if (created == null) {
        	created = new Date();
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = new Date();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
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

    public List<DecisionInput> getInputs() {
        if (inputs == null) {
            inputs = new ArrayList<DecisionInput>();
        }
		return inputs;
	}

    public void setInputs(List<DecisionInput> inputs) {
		this.inputs = inputs;
	}

    public List<DecisionOutput> getOutputs() {
        if (outputs == null) {
            outputs = new ArrayList<DecisionOutput>();
        }
        return outputs;
    }

    public void setOutputs(List<DecisionOutput> outputs) {
        this.outputs = outputs;
    }

    public List<DecisionRule> getRules() {
        return rules;
    }

    public void setRules(List<DecisionRule> rules) {
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
                + ((outputs == null) ? 0 : outputs.hashCode());
        result = prime * result
                + ((inputs == null) ? 0 : inputs.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result
                + ((decisionId == null) ? 0 : decisionId.hashCode());
        result = prime * result
                + ((domainModelUri == null) ? 0 : domainModelUri.hashCode());
        result = prime * result
                + ((hitPolicy == null) ? 0 : hitPolicy.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (outputs == null) {
            if (other.outputs != null)
                return false;
        } else if (!outputs.equals(other.outputs))
            return false;
        if (inputs == null) {
            if (other.inputs != null)
                return false;
        } else if (!inputs.equals(other.inputs))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (decisionId == null) {
            if (other.decisionId != null)
                return false;
        } else if (!decisionId.equals(other.decisionId))
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
