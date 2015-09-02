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
package io.onedecision.engine.decisions.impl.del;

import io.onedecision.engine.decisions.api.DecisionException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationExpression implements DelExpression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DurationExpression.class);

	@Override
	public String compile(String script) {
		Pattern pattern = Pattern
                .compile(".*(P(\\d+Y)*(\\d+M)*(\\d+D)*(\\d+W)*T?(\\d+H)*(\\d+M)*(\\d+S)*).*");
		Matcher matcher = pattern.matcher(script);
        if (matcher.matches()) {
            // Note: 1st capturing group discards all but the duration string
            String inputDuration = matcher.group(1);
			try {
				Duration newDuration = DatatypeFactory.newInstance()
                        .newDuration(convertWeeks(inputDuration));
				String s = script
						.replace(inputDuration, String.valueOf(newDuration
								.getTimeInMillis(new Date())));
                LOGGER.debug("converted script to " + s);
                return compile(s);
			} catch (DatatypeConfigurationException e) {
                throw new DecisionException(e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                if ("P".equals(inputDuration)) {
                    // this is a flaw in the regex and ok to ignore
                    return script;
                } else {
                    throw new DecisionException("Cannot parse inputDuration");
                }
			}
        } else {
            return script;
        }
	}

    // Although ISO 8601 includes PxW to specify a number of weeks this
    // is apparently not supported by Duration class.
    private String convertWeeks(String inputDuration) {
        if (inputDuration.endsWith("W")) {
            BigDecimal weeks = new BigDecimal(inputDuration.substring(1,
                    inputDuration.length() - 1));
            return "P" + weeks.multiply(new BigDecimal(7)) + "D";
        } else {
            return inputDuration;
        }
    }
}
