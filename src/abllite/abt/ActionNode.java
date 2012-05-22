package abllite.abt;
/**
 * Represents the execution of a physical action. 
 *
 * Action nodes are instantiated with a set of prototype parameters. These parameters
 * include literals and variables. Before this node is selected for expansion, the
 * ABT binds the prototype parameters to execution parameters by looking up 
 * behavior-scoped variables. 
 */
public class ActionNode extends ABTNode {

	/** the name of the physical action this action performs */ 
	private String actionName;

	/** prototype parameters, specified as literals and variables */ 
	private Object[] prototypeParameters;
 
	/** parameters specified for action execution. */ 
	private Object[] executionParameters;

	/**
	 * Instantiates an action node that performs the given action name and parameters. 
	 */
	public ActionNode(String actionName, Object[] prototypeParameters) {
		this.actionName = actionName;
		this.prototypeParameters = prototypeParameters;
	}
 
	public String getActionName() {
		return actionName;
	}
	    
	public void bindParameters(Object[] executionParameters) {
		this.executionParameters = executionParameters;
	}
  
	public Object[] getPrototypeParameters() {
		return prototypeParameters; 
	}

	public Object[] getExecutionParameters() {
		return executionParameters; 
	}
	
	public String toString() {  
		return "ActionNode: " + actionName + " (" + nodeStatus + ") " + getPriority(); 
	}
}
