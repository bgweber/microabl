package abllite.abt;



public class GoalNode extends ABTNode {
	

	private String goalName;

	private Object[] parameters = new Object[] {}; 
  
	public GoalNode(String goalName, Object[] parameters) {
		this.goalName = goalName;
		this.parameters = parameters;
	}
	
	public String getGoalName() {
		return goalName;
	} 

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public String toString() { 
		return "GoalNode: " + goalName + " [" + parameters.length + "] (" + nodeStatus + ") " + getPriority(); 
	}
	
	public void childCompleted(ABTNode child) {
		if (child.isSuccess()) {
			setStatus(NodeStatus.Success);
		} 
		else {
			setStatus(NodeStatus.Failure);

			// Try to match other behaviors? 
			Thread.dumpStack();			
			
			// TODO: mark as open, and add attempted behavior to list
		} 
	}
	   
	public Object[] getParameters() {
		return parameters;
	}
}
