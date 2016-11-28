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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationExpression implements DelExpression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DurationExpression.class);

	@Override
    public String compile(String script, String input) {
		Pattern pattern = Pattern
                .compile(".*(P(\\d+Y)*(\\d+M)*(\\d+D)*(\\d+W)*T?(\\d+H)*(\\d+M)*(\\d+S)*).*");
		Matcher matcher = pattern.matcher(script);
        if (matcher.matches()) {
            // Note: 1st capturing group discards all but the duration string
            String inputDuration = matcher.group(1);
			try {
                if ("P".equals(inputDuration)) {
                    // this is a flaw in the regex and ok to ignore
                    return script;
                }

                // org.joda.time.Duration only parses secs and millis, see
                // http://www.joda.org/joda-time/apidocs/org/joda/time/base/AbstractDuration.html#toString()

                // javax.xml.datatype.Duration only works up to Int.MAX millis,
                // see
                // http://docs.oracle.com/javase/7/docs/api/javax/xml/datatype/Duration.html#addTo(java.util.Calendar)

                long years = toMillis(getPart(inputDuration, 'Y'), 'Y');
                // TODO could rely on 365/12 as an approximation but will not
                // currently distinguish months from minutes
                // long months = toMillis(getPart(inputDuration, 'M'),'M');
                long weeks = toMillis(getPart(inputDuration, 'W'), 'W');
                long days = toMillis(getPart(inputDuration, 'D'), 'D');
                long hours = toMillis(getPart(inputDuration, 'H'), 'H');
                long minutes = toMillis(getPart(inputDuration, 'M'), 'M');
                long secs = toMillis(getPart(inputDuration, 'S'), 'S');
                String s = input
                        + " "
                        + script.replace(
                                inputDuration,
                        String.valueOf(years + weeks + days + hours + minutes
                                + secs));
                LOGGER.debug("converted script to " + s);
                return compile(s, input);
            } catch (IllegalArgumentException e) {
                throw new DecisionException("Cannot parse inputDuration");
			}
        } else {
            return script;
        }
	}

    private long toMillis(double val, char part) {
        switch (part) {
        case 'Y':
            return Math.round(val * 1000 * 60 * 60 * 24 * 365);
            // case 'M':
            // return Math.round(val * 1000 * 60 * 60);
        case 'W':
            return Math.round(val * 1000 * 60 * 60 * 24 * 7);
        case 'D':
            return Math.round(val * 1000 * 60 * 60 * 24);
        case 'H':
            return Math.round(val * 1000 * 60 * 60);
        case 'M':
            return Math.round(val * 1000 * 60);
        case 'S':
            return Math.round(val * 1000);
        default:
            return Math.round(val);
        }
    }

    private double getPart(String inputDuration, char part) {
        int end = inputDuration.indexOf(part);
        String s = null;
        if (end != -1) {
            StringBuffer sb = new StringBuffer(inputDuration.substring(0, end))
                    .reverse();
            for (int i = 0; i < sb.length(); i++) {
                if (Character.isDigit(sb.charAt(i))) {
                    continue;
                } else {
                    s = inputDuration.substring(end - i, end);
                    break;
                }
            }
        }
        return s == null ? 0 : Double.parseDouble(s);
    }

    // Although ISO 8601 includes PxW to specify a number of weeks this
    // is apparently not supported by javax.xml.datatype.Duration class.
    protected String convertWeeks(String inputDuration) {
        if (inputDuration.endsWith("W")) {
            BigDecimal weeks = new BigDecimal(inputDuration.substring(1,
                    inputDuration.length() - 1));
            return "P" + weeks.multiply(new BigDecimal(7)) + "D";
        } else {
            return inputDuration;
        }
    }
}
