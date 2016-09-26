//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.26 at 06:59:21 PM BST 
//


package org.omg.spec.dmn._20160719.testcase;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.spec.dmn._20160719.testcase.json.ValueSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * <p>Java class for valueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="valueType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/&gt;
 *         &lt;element name="component" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *               &lt;/extension&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="list"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="item" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueType", propOrder = {
    "value",
    "component",
    "list"
})
@XmlSeeAlso({
    org.omg.spec.dmn._20160719.testcase.TestCases.TestCase.InputNode.class,
    ValueType.Component.class
})
public class ValueType {

    @XmlSchemaType(name = "anySimpleType")
    protected Object value;
    protected java.util.List<ValueType.Component> component;
    protected ValueType.List list;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    @JsonSerialize(using = ValueSerializer.class)
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets the value of the component property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the component property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueType.Component }
     * 
     * 
     */
    public java.util.List<ValueType.Component> getComponent() {
        if (component == null) {
            component = new ArrayList<ValueType.Component>();
        }
        return this.component;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType.List }
     *     
     */
    public ValueType.List getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType.List }
     *     
     */
    public void setList(ValueType.List value) {
        this.list = value;
    }

    public ValueType withValue(Object value) {
        setValue(value);
        return this;
    }

    public ValueType withComponent(ValueType.Component... values) {
        if (values!= null) {
            for (ValueType.Component value: values) {
                getComponent().add(value);
            }
        }
        return this;
    }

    public ValueType withComponent(Collection<ValueType.Component> values) {
        if (values!= null) {
            getComponent().addAll(values);
        }
        return this;
    }

    public ValueType withList(ValueType.List value) {
        setList(value);
        return this;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;extension base="{http://www.omg.org/spec/DMN/20160719/testcase}valueType"&gt;
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Component
        extends ValueType
    {

        @XmlAttribute(name = "name", required = true)
        @XmlSchemaType(name = "anySimpleType")
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

        public ValueType.Component withName(String value) {
            setName(value);
            return this;
        }

        @Override
        public ValueType.Component withValue(Object value) {
            setValue(value);
            return this;
        }

        @Override
        public ValueType.Component withComponent(ValueType.Component... values) {
            if (values!= null) {
                for (ValueType.Component value: values) {
                    getComponent().add(value);
                }
            }
            return this;
        }

        @Override
        public ValueType.Component withComponent(Collection<ValueType.Component> values) {
            if (values!= null) {
                getComponent().addAll(values);
            }
            return this;
        }

        @Override
        public ValueType.Component withList(ValueType.List value) {
            setList(value);
            return this;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="item" type="{http://www.omg.org/spec/DMN/20160719/testcase}valueType" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class List {

        @XmlElement(required = true)
        protected java.util.List<ValueType> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ValueType }
         * 
         * 
         */
        public java.util.List<ValueType> getItem() {
            if (item == null) {
                item = new ArrayList<ValueType>();
            }
            return this.item;
        }

        public ValueType.List withItem(ValueType... values) {
            if (values!= null) {
                for (ValueType value: values) {
                    getItem().add(value);
                }
            }
            return this;
        }

        public ValueType.List withItem(Collection<ValueType> values) {
            if (values!= null) {
                getItem().addAll(values);
            }
            return this;
        }

    }

}
