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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import javax.validation.constraints.Size;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.api.exceptions.InvalidDmnException;

/**
 * Persistent wrapper for DMN model adding repository attributes such as
 * deployment time.
 * 
 * @author Tim Stephenson
 */
@Entity
@Table(name = "OL_DMN_MODEL")
public class DmnModel implements Serializable, Identifiable<Link> {
    private static final long serialVersionUID = 3333702300975742216L;
    
    private static final ObjectFactory objFact = new ObjectFactory();
    
    public static DmnModel newModel() {
        DmnModel model = new DmnModel(objFact.createDefinitions()
                .withId(UUID.randomUUID().toString())
                .withName("New decision model"), "-unknown-tenant-");
        return model; 
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long shortId;

    // @JsonProperty
    private transient List<Link> links;

    @NotNull
    @JsonProperty
    private String name;

    @Column(name = "orig_file")
    @JsonProperty
    private String originalFileName;

    @JsonProperty
    private String description;

    @Column(name = "deployment_msg")
    @JsonProperty
    private String deploymentMessage;

    @Column(name = "tenant_id")
    @NotNull
    @JsonProperty
    private String tenantId;

    @Column(name = "definition_id")
    @NotNull
    @JsonProperty
    private String definitionId;

    @Column(name = "definition_xml")
    @NotNull
    @JsonProperty
    @Lob
    private String definitionXml;

    // @JsonProperty
    // @Embedded
    // private List<String> decisionIds;
    //
    // @JsonProperty
    // @Embedded
    // private List<String> decisionNames;
    //
    // @JsonProperty
    // @Embedded
    // private List<String> bkmIds;
    //
    // @JsonProperty
    // @Embedded
    // private List<String> bkmNames;

    @JsonProperty
    @Lob
    private byte[] image;

    @Size(max = 50)
    @JsonProperty
    private String status;

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

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date lastUpdated;

    private transient Definitions definitions;

    public DmnModel() {
        created = new Date();
        links = new ArrayList<Link>();
    }

    public DmnModel(Definitions model, String tenantId) {
        this(model, model.getName(), tenantId);
    }

    public DmnModel(Definitions model, String deploymentMessage, String tenantId) {
        this();
        setName(model.getName());
        setDefinitionId(model.getId());
        setDefinitions(model);
        setDefinitionXml(serialize(model));
        setTenantId(tenantId);
        setDeploymentMessage(deploymentMessage);
    }

    public DmnModel(String definitionXml, String tenantId)
            throws DecisionException {
        this();
        setDefinitions(deserialize(definitionXml));
        setName(definitions.getName());
        setDefinitionId(definitions.getId());
        setDefinitionXml(definitionXml);
        setTenantId(tenantId);
        setDeploymentMessage(deploymentMessage);
    }

    public DmnModel(String definitionXml, String deploymentMessage,
            byte[] image, String tenantId) throws IOException {
        this(definitionXml, tenantId);
        setDeploymentMessage(deploymentMessage);
    }

    // @JsonIgnore
    public Link getId() {
        return getLink(Link.REL_SELF);
	}

    public void addLink(Link link) {
        links.add(link);
    }

    /**
     * Returns the link with the given rel.
     * 
     * @param rel
     * @return the link with the given rel or {@literal null} if none found.
     */
    public Link getLink(String rel) {
        for (Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    /**
     * Returns all {@link Link}s contained in this resource.
     * 
     * @return
     */
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    public List<Link> getLinks() {
//        if (links.size() == 0) {
//            links.add(new Link(DecisionDmnModelController.class.getAnnotation(
//                    RepositoryRestResource.class).path()
//                    + "/" + shortId));
//        }
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty
    public Long getShortId() {
        return shortId;
    }

    public void setShortId(Long id) {
        this.shortId = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String serialize(Definitions def) {
        JAXBContext context;
        StringWriter stringWriter = new StringWriter();
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Marshaller m = context.createMarshaller();
            Result out = new StreamResult(stringWriter);
            // Since no @XmlRootElement generated for Definitions need to create
            // element wrapper here. See
            // https://weblogs.java.net/blog/kohsuke/archive/2006/03/why_does_jaxb_p.html
            m.marshal(new JAXBElement<Definitions>(new QName(
                    DecisionConstants.DMN_URI,
                    "definitions"),
                    Definitions.class, def), out);
        } catch (JAXBException e) {
            String msg = "Unable to load decision model from stream";
            throw new InvalidDmnException(msg, e);
        }
        return stringWriter.toString();
    }

    @SuppressWarnings("unchecked")
    private Definitions deserialize(@NotNull String definition)
            throws DecisionException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Unmarshaller um = context.createUnmarshaller();

            Object dm = um.unmarshal(new StringReader(definition));
            if (dm instanceof JAXBElement<?>) {
                return ((JAXBElement<Definitions>) dm).getValue();
            } else {
                return (Definitions) dm;
            }
        } catch (JAXBException e) {
            String msg = "Unable to load decision model from stream, typically this means the model does not conform to the published schema.";
            throw new InvalidDmnException(msg, e);
        }
    }

    public Definitions getDefinitions() {
        if (definitions == null && definitionXml != null) {
            definitions = deserialize(definitionXml);
        }
        return definitions;
    }

    public void setDefinitions(Definitions tDef) {
        this.definitions = tDef;
    }

    // /**
    // * On creation the decision ids are read from the DMN file to allow ready
    // * retrieval later.
    // *
    // * @return Returns list of decision ids contained in the model.
    // */
    // public List<String> getDecisionIds() {
    // if (decisionIds == null) {
    // decisionIds = new ArrayList<String>();
    // }
    // return decisionIds;
    // }
    //
    // public void setDecisionIds(List<String> decisionIds) {
    // this.decisionIds = decisionIds;
    // }

    // /**
    // * On creation the decision names are read from the DMN file to allow
    // ready
    // * retrieval later.
    // *
    // * @return Returns list of decision names contained in the model.
    // */
    // public List<String> getDecisionNames() {
    // if (decisionNames == null) {
    // decisionNames = new ArrayList<String>();
    // }
    // return decisionNames;
    // }
    //
    // public void setDecisionNames(List<String> decisionNames) {
    // this.decisionNames = decisionNames;
    // }
    //
    // public List<String> getBusinessKnowledgeModelIds() {
    // if (bkmIds == null) {
    // bkmIds = new ArrayList<String>();
    // }
    // return bkmIds;
    // }
    //
    // public void setBusinessKnowledgeModelIds(List<String> bkmIds) {
    // this.bkmIds = bkmIds;
    // }
    //
    // /**
    // * On creation the BKM names are read from the DMN file to allow ready
    // * retrieval later.
    // *
    // * @return Returns list of BKM names contained in the model.
    // */
    // public List<String> getBusinessKnowledgeModelNames() {
    // if (bkmNames == null) {
    // bkmNames = new ArrayList<String>();
    // }
    // return bkmNames;
    // }
    //
    // public void setBusinessKnowledgeModelNames(List<String> bkmNames) {
    // this.bkmNames = bkmNames;
    // }

    public byte[] getImage() {
		return image;
	}

    public void setImage(byte[] image) {
		this.image = image;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        result = prime * result + ((shortId == null) ? 0 : shortId.hashCode());
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
        if (shortId == null) {
            if (other.shortId != null)
                return false;
        } else if (!shortId.equals(other.shortId))
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
