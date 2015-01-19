package link.omny.decisions.model.adapters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import link.omny.decisions.model.DecisionModelImport;
import link.omny.decisions.model.Expression;
import link.omny.decisions.model.LiteralExpression;
import link.omny.decisions.model.Text;

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
        Element el = findElement(v, "text");
        if (findElement(v, "text") != null) {
            LiteralExpression literal = new LiteralExpression();
            adaptToExpression(v, literal);
            literal.setText(new Text().addContent(el.getTextContent()));
            literal.setImport(v._import);
            literal.setExpressionLanguage(v.expressionLanguage);
            return literal;
        } else {
            return adaptToExpression(v, new UnknownExpression());
        }
    }

    private Expression adaptToExpression(AdaptedExpression v,
            Expression expression) {
        expression.getInputVariable().addAll(v.getInputVariable());
        expression.setItemDefinition(v.getItemDefinition());
        return expression;
    }

    @Override
    public AdaptedExpression marshal(Expression v) throws Exception {
        if (null == v) {
            return null;
        }
        AdaptedExpression AdaptedExpression = new AdaptedExpression();
        if (v instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression) v;
            AdaptedExpression.text = literal.getText();
            AdaptedExpression._import = literal.getImport();
            AdaptedExpression.expressionLanguage = literal
                    .getExpressionLanguage();
        } else if (v instanceof Expression) {
            // continue
        } else {
            System.out.println("Unrecognised expression sub-type: "
                    + v.getClass().getName());
        }
        return AdaptedExpression;
    }

    public Element findElement(AdaptedExpression v, String name) {
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
