package abllite.abt;


public class GoalNode extends ABTNode {
	

	private String goalName;
	
	private Object[] parameters; 
	
	public GoalNode(String goalName, Object[] parameters) {
		this.goalName = goalName;
		this.parameters = parameters;
	}
	
	public String getGoalName() {
		return goalName;
	}
	  
	public Object[] getParameters() {
		return parameters;
	}
	 
	public String toString() {
		return "GoalNode: " + goalName + " (" + nodeStatus + ") " + getPriority(); 
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
}
