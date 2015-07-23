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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "OL_UI_EXPRESSION")
@Component
public class DecisionExpression {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DecisionExpression.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    protected String name;

    @JsonProperty 
    protected String[] expressions;

    @JsonProperty
    protected String label;

    public DecisionExpression() {}

    public DecisionExpression(String name, String[] expressions) {
        setName(name);
        setExpressions(expressions);
    }

	public DecisionExpression(String name, String label,
			String[] expressions) {
		setName(name);
		setLabel(label);
		setExpressions(expressions);
	}

    public String getLabel() {
        if (label == null) {
            return name;
        }
        return label;
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

	public String[] getExpressions() {
		return expressions;
	}

	public void setExpressions(String[] expressions) {
		this.expressions = expressions;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
