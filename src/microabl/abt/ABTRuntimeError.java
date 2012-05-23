package microabl.abt;
/**
 * Specifies an error during execution of an ABL agent. 
 */
public class ABTRuntimeError extends Error {

	/**
	 * Creates an ABT error with the given message. 
	 */
	public ABTRuntimeError(String message) {
		super(message);
	}
}
 