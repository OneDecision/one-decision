package io.onedecision.engine.decisions.api;

import javax.xml.namespace.QName;

public interface DecisionConstants {

    static String DMN_URI = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";

    static String EXPR_FEEL_URI = "http://www.omg.org/spec/FEEL/20140401";

    static final QName CONTEXT = new QName(DecisionConstants.DMN_URI, "context");

    static final QName DEFINITIONS = new QName(DecisionConstants.DMN_URI,
            "definitions");

    static final QName DECISION_TABLE = new QName(DecisionConstants.DMN_URI,
            "decisionTable");

    static final QName INVOCATION = new QName(DecisionConstants.DMN_URI,
            "invocation");

    static final QName LITERAL_EXPRESSION = new QName(
            DecisionConstants.DMN_URI, "literalExpression");

    static final QName FEEL_BOOLEAN = new QName(EXPR_FEEL_URI, "boolean");

    static final QName FEEL_NUMBER = new QName(EXPR_FEEL_URI, "number");

    static final QName FEEL_STRING = new QName(EXPR_FEEL_URI, "string");


}
