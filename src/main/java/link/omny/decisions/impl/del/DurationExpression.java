package link.omny.decisions.impl.del;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import link.omny.decisions.api.DecisionsException;

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
				throw new DecisionsException("Cannot parse inputDuration");
			}

		} else {
			return script;
		}
	}
}
