//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.30 at 01:30:38 PM GMT 
//


package io.onedecision.engine.decisions.model.dmn;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tUnaryTests complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tUnaryTests"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.omg.org/spec/DMN/20151101/dmn.xsd}tDMNElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="expressionLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tUnaryTests", propOrder = {
    "text"
})
public class UnaryTests extends DmnElement implements Serializable {

    private static final long serialVersionUID = 20805629454844459L;
    @XmlElement(required = true)
    protected String text;
    @XmlAttribute(name = "expressionLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String expressionLanguage;

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    public java.util.List<String> getUnaryTests() {
        return Arrays.asList(text.split(","));
    }

    public void setUnaryTests(String... tests) {
        if (tests.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String t : tests) {
            char c = t.trim().charAt(0);
            if (c != '-' && c != '{' && c != '[' && c != '"'
                    && !Character.isDigit(c)) {
                sb.append('"').append(t).append('"').append(',');
            } else {
                sb.append(t).append(',');
            }
        }
        setText(sb.deleteCharAt(sb.length() - 1).toString());
    }

    /**
     * Gets the value of the expressionLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    /**
     * Sets the value of the expressionLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

    public UnaryTests withText(String value) {
        setText(value);
        return this;
    }

    public UnaryTests withUnaryTests(String... tests) {
        setUnaryTests(tests);
        return this;
    }

    public UnaryTests withExpressionLanguage(String value) {
        setExpressionLanguage(value);
        return this;
    }

    @Override
    public UnaryTests withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public UnaryTests withExtensionElements(DmnElement.ExtensionElements value) {
        setExtensionElements(value);
        return this;
    }

    @Override
    public UnaryTests withId(String value) {
        setId(value);
        return this;
    }

    @Override
    public UnaryTests withLabel(String value) {
        setLabel(value);
        return this;
    }

}
