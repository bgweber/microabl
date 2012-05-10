package abllite.abt;

import abllite.prototype.BehaviorPrototype;

public class ParallelNode extends BehaviorNode {

	int stepsCompleted = 0;
	
	public ParallelNode(BehaviorPrototype prototype) {
		super(prototype);		
	} 
	 
	
	public String toString() {
		return "ParallelNode: " + goalName + " (" + nodeStatus + ") " + getPriority(); 
	}
	 
	public void childCompleted(ABTNode child) {
		if (child.isFailure()) {
			setStatus(NodeStatus.Failure);
		} 
		else {
			stepsCompleted++;
			if (stepsCompleted == steps.size()) {
				setStatus(NodeStatus.Success);
			} 
		}
	}
}
