package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;

public class ParallelNode extends BehaviorNode {

	int stepsCompleted = 0;
	
	public ParallelNode(BehaviorPrototype prototype, HashMap<String, Object> parameters) {
		super(prototype, parameters);		
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
