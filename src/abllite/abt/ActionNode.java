package abllite.abt;
 
public class ActionNode extends ABTNode {

	private String actionName;
	 
	private Object[] parameters;
	
	public ActionNode(String actionName, Object[] params) {
		this.actionName = actionName;
		this.parameters = params;
	}

	public String getActionName() {
		return actionName;
	}
	  
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public Object[] getParameters() {
		return parameters; 
	}
	
	public String toString() {  
		return "ActionNode: " + actionName + " (" + nodeStatus + ") " + getPriority(); 
	}
}
