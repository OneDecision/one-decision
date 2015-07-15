
package io.onedecision.engine.decisions.model.dmn;

import java.io.Serializable;
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
        setName(model.getName());
        setDefinitionId(model.getId());
        setTenantId(tenantId);
        setDefinitionXml(xmlString);
    }
    
    public DmnModel() {}

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
}
