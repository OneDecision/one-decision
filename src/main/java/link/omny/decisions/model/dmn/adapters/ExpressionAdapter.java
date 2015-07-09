package link.omny.decisions.model.dmn.adapters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import link.omny.decisions.model.dmn.DecisionModelImport;
import link.omny.decisions.model.dmn.Expression;
import link.omny.decisions.model.dmn.LiteralExpression;
import link.omny.decisions.model.dmn.Text;

import org.springframework.beans.BeanUtils;
import org.w3c.dom.Element;

public class ExpressionAdapter extends
        XmlAdapter<ExpressionAdapter.AdaptedExpression, Expression> {

    // @XmlRootElement(name = "Decision")
    public static class AdaptedExpression extends Expression {

        // @XmlElementRef(name = "inputVariable", namespace =
        // "http://www.omg.org/spec/DMN/20130901", type = JAXBElement.class,
        // required = false)
        // protected List<JAXBElement<Object>> inputVariable;
        // protected QName itemDefinition;

        @XmlElement(name = "text")
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
            literal.setText(new Text().addContent(el.getFirstChild()
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
        try {
            adaptedExpression.getInputVariable().addAll(v.getInputVariable());
        } catch (Exception e) {
            // TODO
            e.getMessage();
        }
        if (v instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression) v;
            adaptedExpression.setId(literal.getId());
            adaptedExpression.setName(literal.getName());
            adaptedExpression.setDescription(literal.getDescription());
            adaptedExpression.text = literal.getText();
        } else if (v instanceof Expression) {
            // continue
        } else {
            // Cannot happen?
            System.out.println("Unrecognised expression sub-type: "
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
