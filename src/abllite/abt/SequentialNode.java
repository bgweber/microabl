package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;
/**
 * Represents a sequential behavior. 
 */
public class SequentialNode extends BehaviorNode {

	/**
	 * Creates a sequential behavior node from the prototype behavior. 
	 * 
	 * @param variables - parameters for the goal the behavior accomplishes. 
	 */
	public SequentialNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		super(prototype, variables);
	}

	/**
	 * When a child step completes, remove the step from the list of remaining steps. If the step failed, then
	 * the behavior fails immediately. Otherwise if there are no further steps, the behavior succeeds. If
	 * there are remaining steps and the step succeeded, then set the behavior status back to open. 
	 */
	public void childCompleted(ABTNode child) {

		if (child.isFailure()) {
			setStatus(NodeStatus.Failure);
		} 
		else {
			steps.remove(child);

			if (steps.size() == 0) {
				setStatus(NodeStatus.Success);
			} 
			else {
				setStatus(NodeStatus.Open);
			}
		}
	}
	
	public String toString() {
		return "SequentialNode: " + getGoalName() + " (" + nodeStatus + ") " + getPriority(); 
	} 
}
