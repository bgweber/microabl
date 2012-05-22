package abllite.prototype;
/**
 * Represents a behavior-scoped variable. 
 */
public class Variable { 

	/** variable name */ 
	private String name;

	/**
	 * Creates a variable with the given name. 
	 */
	public Variable(String name) {
		this.name = name; 
	}
	
	public String getName() {
		return name; 
	}
	 
	public String toString() {
		return "Variable(" + name + ")";
	} 
}
