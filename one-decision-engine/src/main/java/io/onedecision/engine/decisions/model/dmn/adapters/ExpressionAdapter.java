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
package io.onedecision.engine.decisions.model.dmn.adapters;

import io.onedecision.engine.decisions.model.dmn.DecisionModelImport;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.Text;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.w3c.dom.Element;

public class ExpressionAdapter extends
        XmlAdapter<ExpressionAdapter.AdaptedExpression, Expression> {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ExpressionAdapter.class);

	protected ObjectFactory objFact = new ObjectFactory();

    public static class AdaptedExpression extends Expression {

		@XmlElement(name = "text", namespace = Definitions.DMN_1_0)
        protected Text text;
        @XmlElement(name = "Import")
        protected DecisionModelImport _import;
        @XmlAttribute(name = "expressionLanguage")
        @XmlSchemaType(name = "anyURI")
        protected String expressionLanguage;

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }
    }

    @Override
    public Expression unmarshal(AdaptedExpression v) {
        if (null == v) {
            return null;
        }
        if (isLiteral(v)) {
            LiteralExpression literal = new LiteralExpression();
            adaptToExpression(v, literal);
            // TODO replaced el.getTextContent here
            Element el = findElement(v, "text");
			Text txt = objFact.createTLiteralExpressionText();
			literal.setText(txt.addContent(el.getFirstChild()
                    .getNodeValue()));
            literal.setImport(v._import);
            literal.setExpressionLanguage(v.expressionLanguage);
            return literal;
        } else {
            return adaptToExpression(v, new LiteralExpression());
        }
    }

    private Expression adaptToExpression(AdaptedExpression v,
            Expression expression) {
        BeanUtils.copyProperties(v, expression);
        expression.getInputVariable().addAll(v.getInputVariable());
        expression.setItemDefinition(v.getItemDefinition());
        return expression;
    }

    @Override
    public AdaptedExpression marshal(Expression v) throws Exception {
        if (null == v) {
            return null;
        }
        AdaptedExpression adaptedExpression = new AdaptedExpression();
        BeanUtils.copyProperties(v, adaptedExpression);
        adaptedExpression.getInputVariable().addAll(v.getInputVariable());

        if (v instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression) v;
            adaptedExpression.setId(literal.getId());
            adaptedExpression.setName(literal.getName());
            adaptedExpression.setDescription(literal.getDescription());
            adaptedExpression.text = literal.getText();
            if (literal.getText() != null
                    && literal.getText().getContent() != null
                    && literal.getText().getContent().size() > 0) {
                assert (adaptedExpression.getText().getContent().size() == literal
                        .getText().getContent().size());
            }
        } else if (v instanceof Expression) {
            // continue
            LOGGER.warn("Ignoring mashalling of abstract expression: "
                    + v.getId());
        } else {
            // Cannot happen?
            LOGGER.error("Unrecognised expression sub-type: "
                    + v.getClass().getName());
        }
        return adaptedExpression;
    }

    private static boolean isLiteral(final Expression v) {
        return findElement(v, "text") != null;
    }

    private static Element findElement(final Expression v, final String name) {
        Element el = null;
        for (Object o : v.getAny()) {
            if (o instanceof Element
                    && name.equals(((Element) o).getLocalName())) {
                el = (Element) o;
            }
        }
        return el;
    }

}
