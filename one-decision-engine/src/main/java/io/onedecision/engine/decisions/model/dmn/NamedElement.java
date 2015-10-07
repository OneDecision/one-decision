//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.06 at 09:06:03 PM BST 
//


package io.onedecision.engine.decisions.model.dmn;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for tNamedElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tNamedElement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.omg.org/spec/DMN/20130901}tDMNElement"&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNamedElement")
@XmlSeeAlso({
    Definitions.class,
    ElementCollection.class,
    BusinessContextElement.class,
    DrgElement.class,
    ItemDefinition.class,
    InformationItem.class
})
public class NamedElement
    extends DmnElement
    implements Serializable
{

    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    public NamedElement withName(String value) {
        setName(value);
        return this;
    }

    @Override
    public NamedElement withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public NamedElement withAnies(Element... values) {
        if (values!= null) {
            for (Element value: values) {
                getAnies().add(value);
            }
        }
        return this;
    }

    @Override
    public NamedElement withAnies(Collection<Element> values) {
        if (values!= null) {
            getAnies().addAll(values);
        }
        return this;
    }

    @Override
    public NamedElement withId(String value) {
        setId(value);
        return this;
    }

}
