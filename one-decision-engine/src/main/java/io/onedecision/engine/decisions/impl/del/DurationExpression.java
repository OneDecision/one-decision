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

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

public class DurationExpression implements DelExpression {

	@Override
	public String compile(String script) {
		Pattern pattern = Pattern
				.compile(".*(P(\\d+Y)*(\\d+M)*(\\d+D)*T?(\\d+H)*(\\d+M)*(\\d+S)*).*");
		Matcher matcher = pattern.matcher(script);
		if (matcher.matches()) {
			// Note: 1st capturing group discards all but the duration string
			String inputDuration = matcher.group(1);
			System.out.println(inputDuration);
			try {
				Duration newDuration = DatatypeFactory.newInstance()
						.newDuration(inputDuration);
				String s = script
						.replace(inputDuration, String.valueOf(newDuration
								.getTimeInMillis(new Date())));
				System.out.println("converted script to " + s);
				return s;
			} catch (DatatypeConfigurationException e) {
				throw new DecisionException("Cannot parse inputDuration");
			}

		} else {
			return script;
		}
	}
}
