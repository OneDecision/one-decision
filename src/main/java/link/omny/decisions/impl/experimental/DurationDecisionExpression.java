package link.omny.decisions.impl.experimental;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import lombok.Data;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

@Data
public class DurationDecisionExpression {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DurationDecisionExpression.class);

    private Operator operator;
    
    private Duration duration;

    public DurationDecisionExpression(String expr) {
        String op = expr.substring(0, expr.indexOf('P')).trim();
        switch (op) {
        case "<":
            operator = Operator.LT;
            break;
        case "<=":
            operator = Operator.LE;
            break;
        case ">=":
            operator = Operator.GE;
            break;
        case ">":
            operator = Operator.GT;
            break;
        default:
            operator = Operator.EQ;
            break;
        }
        duration = ISOPeriodFormat.standard()
                .parsePeriod(expr.substring(expr.indexOf('P')).trim())
                .toStandardDuration();
    }

    public boolean isTrue(Object bean, String field) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(
                bean.getClass(), field);
        Method readMethod = pd.getReadMethod();
        try {
            Long millis = (Long) readMethod.invoke(bean, new Object[0]);
            switch (operator) {
            case LT:
                return millis < duration.getMillis();
            case LE:
                return millis <= duration.getMillis();
            case EQ:
                return millis == duration.getMillis();
            case GE:
                return millis >= duration.getMillis();
            case GT:
                return millis > duration.getMillis();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

}
