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
package io.onedecision.engine.decisions.converter;

import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.Expression;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.OutputClause;
import io.onedecision.engine.decisions.model.ui.DecisionExpression;
import io.onedecision.engine.decisions.model.ui.DecisionInput;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.decisions.model.ui.DecisionOutput;
import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts between the UI model and DMN serialisation.
 * 
 * @author Tim Stephenson
 */
public class DecisionModelConverter implements
		Converter<DecisionModel, Definitions> {

	private static final String URI_JSON = "http://www.ecma-international.org/ecma-404/";
	private static final String URI_JAVASCRIPT_5 = "http://www.ecma-international.org/ecma-262/5.1/";

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionModelConverter.class);

	protected DomainModelFactory domainFact;

	protected ObjectFactory objFact = new ObjectFactory();

	public Definitions convert(DecisionModel source) {
        Definitions target = objFact.createDefinitions();
		target.setId(createId(source));
        if (source.getName() != null) {
            target.setName(source.getName() + " Model");
        }
		target.setExpressionLanguage(URI_JAVASCRIPT_5);
		target.setNamespace("http://onedecision.io/" + source.getTenantId());
		target.setTypeLanguage(URI_JSON);

        // BusinessKnowledgeModel bkm = objFact.createTBusinessKnowledgeModel();
        // target.getDrgElement().add(objFact.createBusinessKnowledgeModel(bkm));

		List<InformationItem> informationItems = new ArrayList<InformationItem>();

        // TODO is this ok to be null?
        if (source.getDomainModelUri() != null) {
            for (DomainEntity type : getDomainModel(source.getDomainModelUri())
                    .getEntities()) {
                ItemDefinition itemDef = objFact.createItemDefinition();
                itemDef.setId(createTypeDef(type));
                itemDef.setName(type.getName() + " Definition");
                itemDef.setDescription(type.getDescription());

                target.getItemDefinitions().add(itemDef);

                // More indirection - yay!!
                InformationItem informationItem = objFact
                        .createInformationItem();
                informationItem.setId(toId(toCamelCase(type.getName())));
                // TODO these should have own namespace (corresponding to
                // domain)

                // TODO dmn11
                // informationItem.setItemDefinition(new
                // QName(itemDef.getId()));
                // informationItems.add(informationItem);
                // bkm.getInformationItem().add(informationItem);
            }
		}

        Decision decision = objFact.createDecision();
        decision.setId(createDecisionId(source));
        decision.setName(source.getName());
        if (source.getDescription() != null) {
            decision.setDescription(source.getDescription());
        }
        if (source.getQuestion() != null) {
            decision.setQuestion(source.getQuestion());
        }
        if (source.getAllowedAnswers() != null) {
            decision.setAllowedAnswers(source.getAllowedAnswers());
        }


        // TODO is it possible for d output to differ from first dt output?
        // decision.setVariable(objFact.createInformationItem().withId(
        // toCamelCase(source.getOutputs().get(0).getName())));

        if (source.getName() != null) {
            decision.setName(source.getName());
        }
        target.getDrgElements().add(objFact.createDecision(decision));

        DecisionTable dt = objFact.createDecisionTable();
        dt.setId(createDecisionId(source) + "t");
        // TODO DMN11
        // dt.setName(source.getName());
		if (source.getHitPolicy() == null) {
			dt.setHitPolicy(HitPolicy.UNIQUE);
		} else {
			dt.setHitPolicy(HitPolicy.valueOf(source.getHitPolicy()));
		}
        try {
            dt.setPreferredOrientation(DecisionTableOrientation
                    .fromValue(source.getPreferredOrientation()));
        } catch (Exception e) {
            dt.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);
        }
		decision.setExpression(objFact.createDecisionTable(dt));

		for (DecisionInput input : source.getInputs()) {
            dt.getInputs().add(
                    objFact.createInputClause()
                            // TODO This is ok for a DT in isolation but larger
                            // models can result in duplicate ids
                            // .withId(toId(input.getName()))
                            .withLabel(input.getLabel())
                            .withInputExpression(
                                    objFact.createLiteralExpression()
                                            .withText(input.getName())));
        }
		
		for (DecisionOutput output : source.getOutputs()) {
            OutputClause outputClause = objFact.createOutputClause().withName(
                    output.getName());
            dt.getOutputs().add(outputClause);
            if (output.getExpressions() != null) {
                outputClause.withOutputValues(objFact.createUnaryTests()
                        .withUnaryTests(output.getExpressions()));
            }
        }
        
		for (io.onedecision.engine.decisions.model.ui.DecisionRule rule : source.getRules()) {
            DecisionRule dmnRule = objFact.createDecisionRule();
            for (int i = 0; i < rule.getInputEntries().length; i++) {
                dmnRule.getInputEntry().add(
                        objFact.createUnaryTests().withText(
                                rule.getInputEntries()[i]));
            }
            for (int i = 0; i < rule.getOutputEntries().length; i++) {
                dmnRule.getOutputEntry().add(
                        objFact.createLiteralExpression().withText(
                                rule.getOutputEntries()[i]));
            }
            
            dt.withRules(dmnRule);
        }

        return target;
	}

    private String getLeaf(String name) {
        if (name.contains(".")) {
            return name.substring(name.indexOf('.') + 1);
        }
        return name;
    }

    private InformationItem findInformationItem(Definitions definitions,
			String varName) {
        // TODO dmn11
        // for (InformationItem item : definitions.getInformationItems()
        // .getInformationItem()) {
        // System.out.println(String.format("comparing %1$s (%2$s) to %3$s",
        // item.getId(), item.getName(), varName));
        // if (item.getId().equals(varName)) {
        // return item;
        // }
        // }
		return null;
	}

	private void inferRules(DecisionModel source, DecisionTable dt) {
        for (int i = 0; i < source.getInputs().get(0).getExpressions().length; i++) {
			System.out.println("expression  " + i);
            DecisionRule dmRule = objFact.createDecisionRule();

            for (DecisionExpression condExpr : source.getInputs()) {
                LOGGER.debug(String.format("  condition: %1$s (%2$s): %3$s",
                        condExpr.getName(), condExpr.getLabel(),
                        condExpr.getExpressions()[i]));
                List<Expression> l = new ArrayList<Expression>();
				if (condExpr.getExpressions()[i] == null
						|| "".equals(condExpr.getExpressions()[i])
						|| "-".equals(condExpr.getExpressions()[i])) {
                    ;
				} else {
                    // TODO despite adding ok here the IDREFS are not serialized
                    LiteralExpression entry = (LiteralExpression) findConditionEntry(
                            dt,
							condExpr.getExpressions()[i]);
                    // entry.setName(condExpr.getName());
                    l.add(entry);
				}
                // TDOO dmn11
                // JAXBElement<List<Expression>> ruleCond = objFact
                // .createTDecisionRuleCondition(l);
                // dmRule.getCondition().add(ruleCond);
			}
            for (DecisionExpression concExpr : source.getOutputs()) {
                LOGGER.debug(String.format("  conclusion: %1$s %2$s: %3$s",
                        concExpr.getId(), concExpr.getName(),
                        concExpr.getExpressions()[i]));
                List<Expression> l = new ArrayList<Expression>();
				if (concExpr.getExpressions()[i] == null
						|| "".equals(concExpr.getExpressions()[i])
						|| "-".equals(concExpr.getExpressions()[i])) {
				} else {
                    // TODO same issue here as condition above
                    // LiteralExpression entry = (LiteralExpression)
                    // findConclusionEntry(
                    // dt, concExpr.getExpressions()[i]);
                    // // entry.setName(condExpr.getName());
                    // l.add(entry);
				}
                // TODO dmn11
                // JAXBElement<List<Expression>> ruleCond = objFact
                // .createTDecisionRuleConclusion(l);
                // dmRule.getCondition().add(ruleCond);

                try {
                    StringWriter writer = new StringWriter();
                    writeAsXml(dmRule, writer);
                    System.err.println(writer.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

			}
            // TODO dmn11
            // dt.getRule().add(dmRule);
		}

	}

    private Expression findConditionEntry(DecisionTable dt, String string) {
        // TODO dmn11
        // for (DtInput input : dt.getInput()) {
            // input.
            // if (input instanceof LiteralExpression) {
            // String val = ((LiteralExpression) input).getText();
            // System.out.println("  match?: " + string + " = " + val);
            // if (string.equals(val)) {
            // return input;
            // }
            // }
        // }
        throw new DecisionException("Cannot find input for " + string);
    }

    // private Expression findConclusionEntry(DecisionTable dt, String string) {
    // for (Clause clause : dt.getClause()) {
    // for (Expression entry : clause.getOutputDefinition().get) {
    // if (entry instanceof LiteralExpression) {
    // String val = (String) ((LiteralExpression) entry).getText()
    // .getContent().get(0);
    // System.out.println("  match?: " + string + " = " + val);
    // if (string.equals(val)) {
    // return entry;
    // }
    // }
    // }
    // }
    // throw new DecisionException("Cannot find input for " + string);
    // }

	private String createTypeDef(DomainEntity type) {
		return type.getName() + "Def";
	}

    private String createId(DecisionModel source) {
        return toId(source.getId() == null ? UUID.randomUUID().toString()
                : source.getId().toString() + "Model");
    }

    private String createDecisionId(DecisionModel source) {
        return toId(source.getId() == null ? UUID.randomUUID().toString()
                : source.getId() + "_d");
    }

    private String toCamelCase(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private String toId(String name) {
        String id = name.replace(' ', '_').replace('-', '_');
        if (Character.isDigit(id.charAt(0))) {
            id = '_' + id;
        }
        return id;
    }

    private String toHref(String name) {
        return "#" + toCamelCase(name);
    }

	private String inferVarName(String expr) {
		if (expr.indexOf('.') == -1) {
			return expr;
		}
		return expr.substring(0, expr.indexOf('.'));
	}

	public void setDomainModelFactory(DomainModelFactory fact) {
		domainFact = fact;
	}

	public DomainModel getDomainModel(String domainModelUri) {
		if (domainFact == null) {
			throw new IllegalStateException(
					"No domain factory, set one with setDomainFactory()");
		}
		return domainFact.fetchDomain(domainModelUri);
	}

	private void writeAsXml(Object o, Writer writer) throws IOException {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(o.getClass());
			Marshaller m = context.createMarshaller();
			m.marshal(o, writer);
		} catch (JAXBException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
