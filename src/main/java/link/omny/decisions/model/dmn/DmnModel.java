
package link.omny.decisions.model.dmn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
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

    public DmnModel(Definitions model, String xmlString) {
        setName(model.getName());
        setDefinitionId(model.getId());
        setTenantId(tenantId);
        setDefinitionXml(xmlString);
    }

}
