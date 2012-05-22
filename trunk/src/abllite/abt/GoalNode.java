package abllite.abt;

import java.util.HashSet;

import abllite.prototype.BehaviorPrototype;
/**
 * Represents a goal to accomplish. 
 * 
 * Multiple behaviors can be used to accomplish a goal. Behaviors will be retried until a behavior 
 * completes successfully or there are no further behaviors available. However, each behavior 
 * prototype can be expanded at most once for each goal. 
 */
public class GoalNode extends ABTNode {

	/** name of the goal */
	private String goalName;
 
	/** parameters passed to the goal */ 
	protected Object[] parameters = new Object[] {}; 

	/** behavior prototypes that have been used to try to accomplish this goal */ 
	private HashSet<BehaviorPrototype> attemptedBehaviors = new HashSet<BehaviorPrototype>();
  
	/**
	 * Creates a goal node for the specified goal and parameters. 
	 */
	public GoalNode(String goalName, Object[] parameters) {
		this.goalName = goalName;
		this.parameters = parameters;
	}
			 
	/**
	 * If the child behavior succeeds, then set status to success. 
	 * If the child behavior fails, then the goal will be set to open and additional
	 * behavior prototypes will be attempted. 
	 */
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

	public String getGoalName() {
		return goalName;
	} 

	public Object[] getParameters() {
		return parameters;
	}
	
	public String toString() { 
		return "GoalNode: " + goalName + " [" + parameters.length + "] (" + nodeStatus + ") " + getPriority(); 
	}
}
