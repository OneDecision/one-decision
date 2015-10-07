//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.06 at 09:06:03 PM BST 
//


package io.onedecision.engine.decisions.model.dmn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


/**
 * <p>Java class for tDecision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tDecision"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.omg.org/spec/DMN/20130901}tDRGElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="question" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="allowedAnswers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.omg.org/spec/DMN/20130901}InformationItem" minOccurs="0"/&gt;
 *         &lt;element name="informationRequirement" type="{http://www.omg.org/spec/DMN/20130901}tInformationRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="knowledgeRequirement" type="{http://www.omg.org/spec/DMN/20130901}tKnowledgeRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="authorityRequirement" type="{http://www.omg.org/spec/DMN/20130901}tAuthorityRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="supportedObjective" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="impactedPerformanceIndicator" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="decisionMaker" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="decisionOwner" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="usingProcess" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="usingTask" type="{http://www.omg.org/spec/DMN/20130901}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.omg.org/spec/DMN/20130901}Expression" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDecision", propOrder = {
    "question",
    "allowedAnswers",
    "informationItem",
    "informationRequirements",
    "knowledgeRequirements",
    "authorityRequirements",
    "supportedObjectives",
    "impactedPerformanceIndicators",
    "decisionMakers",
    "decisionOwners",
    "usingProcesses",
    "usingTasks",
    "expression"
})
public class Decision
    extends DrgElement
    implements Serializable
{

    protected String question;
    protected String allowedAnswers;
    @XmlElement(name = "InformationItem")
    protected InformationItem informationItem;
    @XmlElement(name = "informationRequirement")
    protected java.util.List<InformationRequirement> informationRequirements;
    @XmlElement(name = "knowledgeRequirement")
    protected java.util.List<KnowledgeRequirement> knowledgeRequirements;
    @XmlElement(name = "authorityRequirement")
    protected java.util.List<AuthorityRequirement> authorityRequirements;
    @XmlElement(name = "supportedObjective")
    protected java.util.List<DmnElementReference> supportedObjectives;
    @XmlElement(name = "impactedPerformanceIndicator")
    protected java.util.List<DmnElementReference> impactedPerformanceIndicators;
    @XmlElement(name = "decisionMaker")
    protected java.util.List<DmnElementReference> decisionMakers;
    @XmlElement(name = "decisionOwner")
    protected java.util.List<DmnElementReference> decisionOwners;
    @XmlElement(name = "usingProcess")
    protected java.util.List<DmnElementReference> usingProcesses;
    @XmlElement(name = "usingTask")
    protected java.util.List<DmnElementReference> usingTasks;
    @XmlElementRef(name = "Expression", namespace = "http://www.omg.org/spec/DMN/20130901", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Expression> expression;

    /**
     * Gets the value of the question property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Sets the value of the question property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuestion(String value) {
        this.question = value;
    }

    /**
     * Gets the value of the allowedAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowedAnswers() {
        return allowedAnswers;
    }

    /**
     * Sets the value of the allowedAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowedAnswers(String value) {
        this.allowedAnswers = value;
    }

    /**
     * Gets the value of the informationItem property.
     * 
     * @return
     *     possible object is
     *     {@link InformationItem }
     *     
     */
    public InformationItem getInformationItem() {
        return informationItem;
    }

    /**
     * Sets the value of the informationItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationItem }
     *     
     */
    public void setInformationItem(InformationItem value) {
        this.informationItem = value;
    }

    /**
     * Gets the value of the informationRequirements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the informationRequirements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInformationRequirements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InformationRequirement }
     * 
     * 
     */
    public java.util.List<InformationRequirement> getInformationRequirements() {
        if (informationRequirements == null) {
            informationRequirements = new ArrayList<InformationRequirement>();
        }
        return this.informationRequirements;
    }

    /**
     * Gets the value of the knowledgeRequirements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the knowledgeRequirements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKnowledgeRequirements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KnowledgeRequirement }
     * 
     * 
     */
    public java.util.List<KnowledgeRequirement> getKnowledgeRequirements() {
        if (knowledgeRequirements == null) {
            knowledgeRequirements = new ArrayList<KnowledgeRequirement>();
        }
        return this.knowledgeRequirements;
    }

    /**
     * Gets the value of the authorityRequirements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorityRequirements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorityRequirements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorityRequirement }
     * 
     * 
     */
    public java.util.List<AuthorityRequirement> getAuthorityRequirements() {
        if (authorityRequirements == null) {
            authorityRequirements = new ArrayList<AuthorityRequirement>();
        }
        return this.authorityRequirements;
    }

    /**
     * Gets the value of the supportedObjectives property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedObjectives property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedObjectives().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getSupportedObjectives() {
        if (supportedObjectives == null) {
            supportedObjectives = new ArrayList<DmnElementReference>();
        }
        return this.supportedObjectives;
    }

    /**
     * Gets the value of the impactedPerformanceIndicators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the impactedPerformanceIndicators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImpactedPerformanceIndicators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getImpactedPerformanceIndicators() {
        if (impactedPerformanceIndicators == null) {
            impactedPerformanceIndicators = new ArrayList<DmnElementReference>();
        }
        return this.impactedPerformanceIndicators;
    }

    /**
     * Gets the value of the decisionMakers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decisionMakers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecisionMakers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getDecisionMakers() {
        if (decisionMakers == null) {
            decisionMakers = new ArrayList<DmnElementReference>();
        }
        return this.decisionMakers;
    }

    /**
     * Gets the value of the decisionOwners property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decisionOwners property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecisionOwners().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getDecisionOwners() {
        if (decisionOwners == null) {
            decisionOwners = new ArrayList<DmnElementReference>();
        }
        return this.decisionOwners;
    }

    /**
     * Gets the value of the usingProcesses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingProcesses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingProcesses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getUsingProcesses() {
        if (usingProcesses == null) {
            usingProcesses = new ArrayList<DmnElementReference>();
        }
        return this.usingProcesses;
    }

    /**
     * Gets the value of the usingTasks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingTasks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingTasks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DmnElementReference }
     * 
     * 
     */
    public java.util.List<DmnElementReference> getUsingTasks() {
        if (usingTasks == null) {
            usingTasks = new ArrayList<DmnElementReference>();
        }
        return this.usingTasks;
    }

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LiteralExpression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
     *     {@link JAXBElement }{@code <}{@link Invocation }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionDefinition }{@code >}
     *     {@link JAXBElement }{@code <}{@link io.onedecision.engine.decisions.model.dmn.List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecisionTable }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link LiteralExpression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
     *     {@link JAXBElement }{@code <}{@link Invocation }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionDefinition }{@code >}
     *     {@link JAXBElement }{@code <}{@link io.onedecision.engine.decisions.model.dmn.List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecisionTable }{@code >}
     *     
     */
    public void setExpression(JAXBElement<? extends Expression> value) {
        this.expression = value;
    }

    public Decision withQuestion(String value) {
        setQuestion(value);
        return this;
    }

    public Decision withAllowedAnswers(String value) {
        setAllowedAnswers(value);
        return this;
    }

    public Decision withInformationItem(InformationItem value) {
        setInformationItem(value);
        return this;
    }

    public Decision withInformationRequirements(InformationRequirement... values) {
        if (values!= null) {
            for (InformationRequirement value: values) {
                getInformationRequirements().add(value);
            }
        }
        return this;
    }

    public Decision withInformationRequirements(Collection<InformationRequirement> values) {
        if (values!= null) {
            getInformationRequirements().addAll(values);
        }
        return this;
    }

    public Decision withKnowledgeRequirements(KnowledgeRequirement... values) {
        if (values!= null) {
            for (KnowledgeRequirement value: values) {
                getKnowledgeRequirements().add(value);
            }
        }
        return this;
    }

    public Decision withKnowledgeRequirements(Collection<KnowledgeRequirement> values) {
        if (values!= null) {
            getKnowledgeRequirements().addAll(values);
        }
        return this;
    }

    public Decision withAuthorityRequirements(AuthorityRequirement... values) {
        if (values!= null) {
            for (AuthorityRequirement value: values) {
                getAuthorityRequirements().add(value);
            }
        }
        return this;
    }

    public Decision withAuthorityRequirements(Collection<AuthorityRequirement> values) {
        if (values!= null) {
            getAuthorityRequirements().addAll(values);
        }
        return this;
    }

    public Decision withSupportedObjectives(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getSupportedObjectives().add(value);
            }
        }
        return this;
    }

    public Decision withSupportedObjectives(Collection<DmnElementReference> values) {
        if (values!= null) {
            getSupportedObjectives().addAll(values);
        }
        return this;
    }

    public Decision withImpactedPerformanceIndicators(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getImpactedPerformanceIndicators().add(value);
            }
        }
        return this;
    }

    public Decision withImpactedPerformanceIndicators(Collection<DmnElementReference> values) {
        if (values!= null) {
            getImpactedPerformanceIndicators().addAll(values);
        }
        return this;
    }

    public Decision withDecisionMakers(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getDecisionMakers().add(value);
            }
        }
        return this;
    }

    public Decision withDecisionMakers(Collection<DmnElementReference> values) {
        if (values!= null) {
            getDecisionMakers().addAll(values);
        }
        return this;
    }

    public Decision withDecisionOwners(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getDecisionOwners().add(value);
            }
        }
        return this;
    }

    public Decision withDecisionOwners(Collection<DmnElementReference> values) {
        if (values!= null) {
            getDecisionOwners().addAll(values);
        }
        return this;
    }

    public Decision withUsingProcesses(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getUsingProcesses().add(value);
            }
        }
        return this;
    }

    public Decision withUsingProcesses(Collection<DmnElementReference> values) {
        if (values!= null) {
            getUsingProcesses().addAll(values);
        }
        return this;
    }

    public Decision withUsingTasks(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getUsingTasks().add(value);
            }
        }
        return this;
    }

    public Decision withUsingTasks(Collection<DmnElementReference> values) {
        if (values!= null) {
            getUsingTasks().addAll(values);
        }
        return this;
    }

    public Decision withExpression(JAXBElement<? extends Expression> value) {
        setExpression(value);
        return this;
    }

    public Decision withDecisionTable(DecisionTable dt) {
        setExpression(new JAXBElement(new QName(
                "http://www.omg.org/spec/DMN/20130901", "DecisionTable"),
                DecisionTable.class, dt));
        return this;
    }

    @Override
    public Decision withName(String value) {
        setName(value);
        return this;
    }

    @Override
    public Decision withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public Decision withAnies(Element... values) {
        if (values!= null) {
            for (Element value: values) {
                getAnies().add(value);
            }
        }
        return this;
    }

    @Override
    public Decision withAnies(Collection<Element> values) {
        if (values!= null) {
            getAnies().addAll(values);
        }
        return this;
    }

    @Override
    public Decision withId(String value) {
        setId(value);
        return this;
    }

}
