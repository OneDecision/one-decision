//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.30 at 01:30:38 PM GMT 
//


package io.onedecision.engine.decisions.model.dmn;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.api.DecisionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tDecision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tDecision"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDRGElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="question" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="allowedAnswers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="variable" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tInformationItem" minOccurs="0"/&gt;
 *         &lt;element name="informationRequirement" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tInformationRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="knowledgeRequirement" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tKnowledgeRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="authorityRequirement" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tAuthorityRequirement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="supportedObjective" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="impactedPerformanceIndicator" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="decisionMaker" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="decisionOwner" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="usingProcess" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="usingTask" type="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}tDMNElementReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.omg.org/spec/DMN/20151101/dmn11.xsd}expression" minOccurs="0"/&gt;
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
@XmlRootElement(name = "decision")
@XmlType(name = "tDecision", propOrder = {
    "question",
    "allowedAnswers",
    "variable",
    "informationRequirement",
    "knowledgeRequirement",
    "authorityRequirement",
    "supportedObjective",
    "impactedPerformanceIndicator",
    "decisionMaker",
    "decisionOwner",
    "usingProcess",
    "usingTask",
    "expression"
})
public class Decision extends DrgElement implements
        InformationRequirementReference,
        Serializable {

    private static final long serialVersionUID = 4761130984661556080L;
    private static ObjectFactory objFact = new ObjectFactory();
    protected String question;
    protected String allowedAnswers;
    protected InformationItem variable;
    protected java.util.List<InformationRequirement> informationRequirement;
    protected java.util.List<KnowledgeRequirement> knowledgeRequirement;
    protected java.util.List<AuthorityRequirement> authorityRequirement;
    protected java.util.List<DmnElementReference> supportedObjective;
    protected java.util.List<DmnElementReference> impactedPerformanceIndicator;
    protected java.util.List<DmnElementReference> decisionMaker;
    protected java.util.List<DmnElementReference> decisionOwner;
    protected java.util.List<DmnElementReference> usingProcess;
    protected java.util.List<DmnElementReference> usingTask;
    @XmlElementRef(name = "expression", namespace = "http://www.omg.org/spec/DMN/20151101/dmn11.xsd", type = JAXBElement.class, required = false)
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
     * The instance of InformationItem that stores the result of this Decision.
     * 
     * @return possible object is {@link InformationItem }
     */
    public InformationItem getVariable() {
        return variable;
    }

    /**
     * Synonym for {@link getVariable }
     * 
     * @return {@link InformationItem }
     */
    public DmnElement getInformationItem() {
        return getVariable();
    }

    /**
     * Sets the value of the variable property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationItem }
     *     
     */
    public void setVariable(InformationItem value) {
        this.variable = value;
    }

    /**
     * Gets the value of the informationRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the informationRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInformationRequirement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InformationRequirement }
     * 
     * 
     */
    public java.util.List<InformationRequirement> getInformationRequirement() {
        if (informationRequirement == null) {
            informationRequirement = new ArrayList<InformationRequirement>();
        }
        return this.informationRequirement;
    }

    /**
     * Gets the value of the knowledgeRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the knowledgeRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKnowledgeRequirement().add(newItem);
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
        if (knowledgeRequirement == null) {
            knowledgeRequirement = new ArrayList<KnowledgeRequirement>();
        }
        return this.knowledgeRequirement;
    }

    /**
     * Gets the value of the authorityRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorityRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorityRequirement().add(newItem);
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
        if (authorityRequirement == null) {
            authorityRequirement = new ArrayList<AuthorityRequirement>();
        }
        return this.authorityRequirement;
    }

    /**
     * Gets the value of the supportedObjective property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedObjective property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedObjective().add(newItem);
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
        if (supportedObjective == null) {
            supportedObjective = new ArrayList<DmnElementReference>();
        }
        return this.supportedObjective;
    }

    /**
     * Gets the value of the impactedPerformanceIndicator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the impactedPerformanceIndicator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImpactedPerformanceIndicator().add(newItem);
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
        if (impactedPerformanceIndicator == null) {
            impactedPerformanceIndicator = new ArrayList<DmnElementReference>();
        }
        return this.impactedPerformanceIndicator;
    }

    /**
     * Gets the value of the decisionMaker property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decisionMaker property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecisionMaker().add(newItem);
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
        if (decisionMaker == null) {
            decisionMaker = new ArrayList<DmnElementReference>();
        }
        return this.decisionMaker;
    }

    /**
     * Gets the value of the decisionOwner property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decisionOwner property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecisionOwner().add(newItem);
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
        if (decisionOwner == null) {
            decisionOwner = new ArrayList<DmnElementReference>();
        }
        return this.decisionOwner;
    }

    /**
     * Gets the value of the usingProcess property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingProcess property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingProcess().add(newItem);
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
        if (usingProcess == null) {
            usingProcess = new ArrayList<DmnElementReference>();
        }
        return this.usingProcess;
    }

    /**
     * Gets the value of the usingTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usingTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsingTask().add(newItem);
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
        if (usingTask == null) {
            usingTask = new ArrayList<DmnElementReference>();
        }
        return this.usingTask;
    }

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FunctionDefinition }{@code >}
     *     {@link JAXBElement }{@code <}{@link DecisionTable }{@code >}
     *     {@link JAXBElement }{@code <}{@link Invocation }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralExpression }{@code >}
     *     {@link JAXBElement }{@code <}{@link io.onedecision.engine.decisions.model.dmn.List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
     *     
     */
    public JAXBElement<? extends Expression> getExpression() {
        return expression;
    }

    public DecisionTable getDecisionTable() {
        if (expression == null) { 
            return null; 
        } else if (expression.getValue() instanceof DecisionTable) {
            return (DecisionTable) expression.getValue(); 
        } else { 
            throw new DecisionException("Expression is not a decision table"); 
        }
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
     *     {@link JAXBElement }{@code <}{@link io.onedecision.engine.decisions.model.dmn.List }{@code >}
     *     {@link JAXBElement }{@code <}{@link Context }{@code >}
     *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
     *     {@link JAXBElement }{@code <}{@link Relation }{@code >}
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

    public Decision withVariable(InformationItem value) {
        setVariable(value);
        return this;
    }

    public Decision withInformationItem(InformationItem value) {
        setVariable(value);
        return this;
    }

    public Decision withInformationRequirements(InformationRequirement... values) {
        if (values!= null) {
            for (InformationRequirement value: values) {
                getInformationRequirement().add(value);
            }
        }
        return this;
    }

    public Decision withInformationRequirements(
            InformationRequirementReference... values) {
        if (values != null) {
            for (InformationRequirementReference value : values) {
                if (value instanceof Decision){
                    getInformationRequirement().add(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision((Decision) value));
                } else if (value instanceof InputData) {
                    getInformationRequirement().add(
                            objFact.createInformationRequirement()
                                    .withRequiredInput((InputData) value));
                }
            }
        }
        return this;
    }

    public Decision withInformationRequirements(Collection<InformationRequirement> values) {
        if (values!= null) {
            getInformationRequirement().addAll(values);
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

    public Decision withSupportedObjective(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getSupportedObjectives().add(value);
            }
        }
        return this;
    }

    public Decision withSupportedObjective(Collection<DmnElementReference> values) {
        if (values!= null) {
            getSupportedObjectives().addAll(values);
        }
        return this;
    }

    public Decision withImpactedPerformanceIndicator(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getImpactedPerformanceIndicators().add(value);
            }
        }
        return this;
    }

    public Decision withImpactedPerformanceIndicator(Collection<DmnElementReference> values) {
        if (values!= null) {
            getImpactedPerformanceIndicators().addAll(values);
        }
        return this;
    }

    public Decision withDecisionMaker(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getDecisionMakers().add(value);
            }
        }
        return this;
    }

    public Decision withDecisionMaker(Collection<DmnElementReference> values) {
        if (values!= null) {
            getDecisionMakers().addAll(values);
        }
        return this;
    }

    public Decision withDecisionOwner(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getDecisionOwners().add(value);
            }
        }
        return this;
    }

    public Decision withDecisionOwner(Collection<DmnElementReference> values) {
        if (values!= null) {
            getDecisionOwners().addAll(values);
        }
        return this;
    }

    public Decision withUsingProcess(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getUsingProcesses().add(value);
            }
        }
        return this;
    }

    public Decision withUsingProcess(Collection<DmnElementReference> values) {
        if (values!= null) {
            getUsingProcesses().addAll(values);
        }
        return this;
    }

    public Decision withUsingTask(DmnElementReference... values) {
        if (values!= null) {
            for (DmnElementReference value: values) {
                getUsingTasks().add(value);
            }
        }
        return this;
    }

    public Decision withUsingTask(Collection<DmnElementReference> values) {
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
        setExpression(new JAXBElement(DecisionConstants.DECISION_TABLE,
                DecisionTable.class, dt));
        return this;
    }

    public Decision withInvocation(Invocation invocation) {
        setExpression(new JAXBElement(DecisionConstants.INVOCATION,
                Invocation.class, invocation));
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
    public Decision withExtensionElements(io.onedecision.engine.decisions.model.dmn.DmnElement.ExtensionElements value) {
        setExtensionElements(value);
        return this;
    }

    @Override
    public Decision withId(String value) {
        setId(value);
        return this;
    }

    @Override
    public Decision withLabel(String value) {
        setLabel(value);
        return this;
    }

}
