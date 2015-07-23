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

package io.onedecision.engine.decisions.model.dmn;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 
 * @author Tim Stephenson
 */
@Entity
@Table(name = "OL_DMN_MODEL")
public class DmnModel  implements Serializable {
    private static final long serialVersionUID = 3333702300975742216L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    private String name;

    @JsonProperty
    private String originalFileName;

    @JsonProperty
    private String deploymentMessage;

    @NotNull
    @JsonProperty
    private String tenantId;

    @NotNull
    @JsonProperty
    private String definitionId;

    @NotNull
    @JsonProperty
    @Lob
    private String definitionXml;

    @JsonProperty
    @Lob
    private byte[] image;

    /**
     * The time the contact is created.
     * 
     * Generally this field is managed by the application but this is not
     * rigidly enforced as exceptions such as data migration do exist.
     */
    @Temporal(TemporalType.TIMESTAMP)
    // Since this is SQL 92 it should be portable
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    @JsonProperty
    private Date created;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date lastUpdated;

    public DmnModel(Definitions model, String xmlString) {
        this();
        setName(model.getName());
        setDefinitionId(model.getId());
        setTenantId(tenantId);
        setDefinitionXml(xmlString);
    }
    
    public DmnModel() {
        created = new Date();
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

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getDeploymentMessage() {
		return deploymentMessage;
	}

	public void setDeploymentMessage(String deploymentMessage) {
		this.deploymentMessage = deploymentMessage;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}

	public String getDefinitionXml() {
		return definitionXml;
	}

	public void setDefinitionXml(String definitionXml) {
		this.definitionXml = definitionXml;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result
                + ((definitionId == null) ? 0 : definitionId.hashCode());
        result = prime * result
                + ((definitionXml == null) ? 0 : definitionXml.hashCode());
        result = prime
                * result
                + ((deploymentMessage == null) ? 0 : deploymentMessage
                        .hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + Arrays.hashCode(image);
        result = prime * result
                + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime
                * result
                + ((originalFileName == null) ? 0 : originalFileName.hashCode());
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
        DmnModel other = (DmnModel) obj;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (definitionId == null) {
            if (other.definitionId != null)
                return false;
        } else if (!definitionId.equals(other.definitionId))
            return false;
        if (definitionXml == null) {
            if (other.definitionXml != null)
                return false;
        } else if (!definitionXml.equals(other.definitionXml))
            return false;
        if (deploymentMessage == null) {
            if (other.deploymentMessage != null)
                return false;
        } else if (!deploymentMessage.equals(other.deploymentMessage))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (!Arrays.equals(image, other.image))
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
        if (originalFileName == null) {
            if (other.originalFileName != null)
                return false;
        } else if (!originalFileName.equals(other.originalFileName))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }
}
