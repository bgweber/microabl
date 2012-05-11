package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;

public class ParallelNode extends BehaviorNode {

	private int stepsCompleted = 0;
	private int numberNeededForSuccess; 
	
	
	public ParallelNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		super(prototype, variables);				
		numberNeededForSuccess = prototype.getNumberNeededForSuccess() > 0 ? prototype.getNumberNeededForSuccess() : steps.size();
	} 
 
	public String toString() {
		return "ParallelNode: " + getGoalName() + " (" + nodeStatus + ") " + getPriority(); 
	}
 
	public void childCompleted(ABTNode child) {
		if (child.isFailure()) {
			setStatus(NodeStatus.Failure);
		}  
		else { 
			stepsCompleted++; 
			if (stepsCompleted >= numberNeededForSuccess) {
				setStatus(NodeStatus.Success);
			} 
		}
	}
}
