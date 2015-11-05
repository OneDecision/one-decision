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
import io.onedecision.engine.decisions.model.dmn.BusinessKnowledgeModel;
import io.onedecision.engine.decisions.model.dmn.Clause;
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
import io.onedecision.engine.decisions.model.dmn.Text;
import io.onedecision.engine.decisions.model.ui.DecisionExpression;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

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
		Definitions target = objFact.createTDefinitions();
		target.setId(createId(source));
		target.setName(source.getName() + " Model");
		target.setExpressionLanguage(URI_JAVASCRIPT_5);
		target.setNamespace("http://onedecision.io/" + source.getTenantId());
		target.setTypeLanguage(URI_JSON);

		BusinessKnowledgeModel bkm = objFact.createTBusinessKnowledgeModel();
		target.getDrgElement().add(objFact.createBusinessKnowledgeModel(bkm));

		List<InformationItem> informationItems = new ArrayList<InformationItem>();
		for (DomainEntity type : getDomainModel(source.getDomainModelUri())
				.getEntities()) {
			ItemDefinition itemDef = objFact.createTItemDefinition();
			itemDef.setId(createTypeDef(type));
			itemDef.setName(type.getName() + " Definition");
			itemDef.setDescription(type.getDescription());

			target.getItemDefinition().add(itemDef);

			// More indirection - yay!!
			InformationItem informationItem = objFact.createTInformationItem();
			informationItem.setId(toCamelCase(type.getName()));
			// TODO these should have own namespace (corresponding to domain)
			informationItem.setItemDefinition(new QName(itemDef.getId()));
			informationItems.add(informationItem);
			bkm.getInformationItem().add(informationItem);
		}

		Decision decision = objFact.createTDecision();
		decision.setId(source.getId() == null ? UUID.randomUUID() + "_d"
				: source.getId().toString() + "_d");
		decision.setName(source.getName() + " Decision");
		target.getDrgElement().add(objFact.createDecision(decision));

		DecisionTable dt = objFact.createTDecisionTable();
		dt.setId(source.getId() == null ? UUID.randomUUID() + "_dt"
				: source.getId().toString() + "_dt");
		dt.setName(source.getName());
		if (source.getHitPolicy() == null) {
			dt.setHitPolicy(HitPolicy.UNIQUE);
		} else {
			dt.setHitPolicy(HitPolicy.valueOf(source.getHitPolicy()));
		}
		dt.setPreferedOrientation(DecisionTableOrientation.RULE_AS_COLUMN);
		decision.setExpression(objFact.createDecisionTable(dt));

		for (DecisionExpression condition : source.getConditions()) {
            List<String> added = new ArrayList<String>();
            String inputVarName = inferVarName(condition.getName());
			InformationItem inputVar = findInformationItem(target, inputVarName);
			JAXBElement<Object> inputExpr = objFact
					.createTExpressionInputVariable(inputVar);

			List<Expression> entries = new ArrayList<Expression>();
			for (String expr : condition.getExpressions()) {
                if (!added.contains(expr)) {
                    added.add(expr);

                    LiteralExpression le = objFact.createTLiteralExpression();
                    le.setId(createId(dt, source, source.getConditions(),
                            condition, expr));
                    // le.setName(condition.getName());
                    le.getInputVariable().add(inputExpr);

                    Text txt = objFact.createTLiteralExpressionText();
                    txt.getContent().add(expr);
                    le.setText(txt);
                    entries.add(le);
                }
			}
			Clause clause = objFact.createTClause();
            clause.setName(getLeaf(condition.getName()));
			clause.getInputEntry().addAll(entries);

			Expression inExpr = objFact.createTLiteralExpression();
			clause.setInputExpression(inExpr);
			inExpr.setId(createId(dt, source, condition));
			inExpr.setName(condition.getName());
			inExpr.setDescription(condition.getLabel());
			inExpr.getInputVariable().add(
					objFact.createTExpressionInputVariable(inputVar));

			dt.getClause().add(clause);
		}

		for (DecisionExpression conclusion : source.getConclusions()) {
            List<String> added = new ArrayList<String>();
			Clause clause = objFact.createTClause();
            clause.setName(getLeaf(conclusion.getName()));
			// TODO customer namespace?
			clause.setOutputDefinition(new QName(inferVarName(conclusion
					.getName())));

			for (String expr : conclusion.getExpressions()) {
                if (!added.contains(expr)) {
                    added.add(expr);
                    LiteralExpression le = objFact.createTLiteralExpression();
                    le.setId(createId(dt, source, source.getConclusions(),
                            conclusion, expr));
                    le.setDescription(conclusion.getLabel());
                    // TODO appear unable to set output definition as expected
                    // by DecisionService.getScript
                    le.setItemDefinition(new QName(inferVarName(conclusion
                            .getName())));
                    Text txt = objFact.createTLiteralExpressionText();
                    txt.getContent().add(conclusion.getName() + " = " + expr);
                    le.setText(txt);
                    clause.getOutputEntry().add(le);
                }
			}

			dt.getClause().add(clause);
		}

        inferRules(source, dt);

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
		for (InformationItem item : definitions.getBusinessKnowledgeModel()
				.getInformationItem()) {
            System.out.println(String.format("comparing %1$s (%2$s) to %3$s",
                    item.getId(), item.getName(), varName));
            if (item.getId().equals(varName)) {
				return item;
			}
		}
		return null;
	}

	private void inferRules(DecisionModel source, DecisionTable dt) {
		for (int i = 0; i < source.getConditions().get(0).getExpressions().length; i++) {
			System.out.println("expression  " + i);
			DecisionRule dmRule = objFact.createTDecisionRule();

			for (DecisionExpression condExpr : source.getConditions()) {
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
                JAXBElement<List<Expression>> ruleCond = objFact
                        .createTDecisionRuleCondition(l);
				dmRule.getCondition().add(ruleCond);
			}
			for (DecisionExpression concExpr : source.getConclusions()) {
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
                JAXBElement<List<Expression>> ruleCond = objFact
                        .createTDecisionRuleConclusion(l);
				dmRule.getCondition().add(ruleCond);

                try {
                    StringWriter writer = new StringWriter();
                    writeAsXml(dmRule, writer);
                    System.err.println(writer.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

			}
			dt.getRule().add(dmRule);
		}

	}

    private Expression findConditionEntry(DecisionTable dt, String string) {
        for (Clause clause : dt.getClause()) {
            for (Expression entry : clause.getInputEntry()) {
                if (entry instanceof LiteralExpression) {
                    String val = (String) ((LiteralExpression) entry).getText()
                            .getContent().get(0);
                    // System.out.println("  match?: " + string + " = " + val);
                    if (string.equals(val)) {
                        return entry;
                    }
                }
            }
        }
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
        return source.getId() == null ? UUID.randomUUID().toString()
                : toCamelCase(source.getId().toString()) + "Model";
    }

	private String createId(DecisionTable dt, DecisionModel source,
			DecisionExpression condition) {
		return dt.getId() + "_c" + source.getConditions().indexOf(condition);
	}

	private String createId(DecisionTable dt, DecisionModel source,
            List<? extends DecisionExpression> conditions,
            DecisionExpression condition, String expr) {
		List<String> exprs = Arrays.asList(condition.getExpressions());
		return dt.getId() + "_c" + conditions.indexOf(condition) + "_e"
				+ exprs.indexOf(expr);
	}

	private String toCamelCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
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
