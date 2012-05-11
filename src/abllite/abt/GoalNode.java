package abllite.abt;

import java.util.HashSet;

import abllite.prototype.BehaviorPrototype;



public class GoalNode extends ABTNode {
	

	private String goalName;

	private Object[] parameters = new Object[] {}; 
	 
	private HashSet<BehaviorPrototype> attemptedBehaviors = new HashSet<BehaviorPrototype>();
  
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
			setStatus(NodeStatus.Open);
		} 
	} 
 
	public void attemptingBehavior(BehaviorPrototype behavior) {
		attemptedBehaviors.add(behavior);
	} 

	public boolean hasAttempted(BehaviorPrototype behavior) {
		return attemptedBehaviors.contains(behavior);
	}
	   
	public Object[] getParameters() {
		return parameters;
	}
}
