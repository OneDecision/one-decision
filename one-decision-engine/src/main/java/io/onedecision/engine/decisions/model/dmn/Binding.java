//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.30 at 01:30:38 PM GMT 
//


package io.onedecision.engine.decisions.model.dmn;

import io.onedecision.engine.decisions.api.DecisionConstants;

import java.io.Serializable;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tBinding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tBinding"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}expression" minOccurs="0"/&gt;
 *         &lt;element name="parameter" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tInformationItem"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBinding", propOrder = {
    "expression",
    "parameter"
})
public class Binding implements Serializable {

    private static final long serialVersionUID = -9157873528936509338L;

    private static ObjectFactory objFact = new ObjectFactory();

    @XmlElementRef(name = "expression", namespace = "http://www.omg.org/spec/DMN/20151101/dmn11.xsd", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Expression> expression;
    @XmlElement(required = true)
    protected InformationItem parameter;

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FunctionDefinition }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecisionTable }{@code >}
     *     {@link JAXBElement }{@code <}{@link Invocation }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralExpression }{@code >}
     *     {@link JAXBElement }{@code <}{@link List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
     *     
     */
    public JAXBElement<? extends Expression> getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FunctionDefinition }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecisionTable }{@code >}
     *     {@link JAXBElement }{@code <}{@link Invocation }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralExpression }{@code >}
     *     {@link JAXBElement }{@code <}{@link List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
     *     
     */
    public void setExpression(JAXBElement<? extends Expression> value) {
        this.expression = value;
    }

    public void setLiteralExpression(LiteralExpression le) {
        this.expression = new JAXBElement(DecisionConstants.LITERAL_EXPRESSION,
                Context.class, le);
    }

    /**
     * Gets the value of the parameter property.
     * 
     * @return
     *     possible object is
     *     {@link InformationItem }
     *     
     */
    public InformationItem getParameter() {
        return parameter;
    }

    /**
     * Sets the value of the parameter property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationItem }
     *     
     */
    public void setParameter(InformationItem value) {
        this.parameter = value;
    }

    public Binding withExpression(JAXBElement<? extends Expression> value) {
        setExpression(value);
        return this;
    }

    public Binding withLiteralExpression(LiteralExpression le) {
        setLiteralExpression(le);
        return this;
    }

    public Binding withLiteralExpression(String text) {
        setLiteralExpression(objFact.createLiteralExpression().withText(text));
        return this;
    }

    public Binding withParameter(InformationItem item) {
        setParameter(item);
        return this;
    }

    public Binding withParameter(String itemName) {
        setParameter(objFact.createInformationItem().withName(itemName));
        return this;
    }
}
