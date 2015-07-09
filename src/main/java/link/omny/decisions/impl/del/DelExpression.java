package link.omny.decisions.impl.del;

/**
 * Defines a Decision Expression Language expression. This is intentionally not
 * referred to as FEEL from the DMN specification at this stage.
 */
public interface DelExpression {

	String compile(String script);

}
