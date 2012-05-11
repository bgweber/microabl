package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;

public class SequentialNode extends BehaviorNode {

	public SequentialNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		super(prototype, variables);
	}
	 
	  
	public String toString() {
		return "SequentialNode: " + getGoalName() + " (" + nodeStatus + ") " + getPriority(); 
	} 
	 
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
}
