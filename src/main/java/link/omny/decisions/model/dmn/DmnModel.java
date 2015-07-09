
package link.omny.decisions.model.dmn;

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

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 
 * @author Tim Stephenson
 */
@Data
@NoArgsConstructor
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

}
