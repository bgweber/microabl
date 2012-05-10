package abllite.abt;

import java.util.ArrayList;

import abllite.prototype.BehaviorPrototype;

public class SequentialNode extends BehaviorNode {

	int stepsCompleted = 0;
 	
	public SequentialNode(BehaviorPrototype prototype) {
		super(prototype);
	}
	
	
	public String toString() {
		return "SequentialNode: " + goalName + " (" + nodeStatus + ") " + getPriority(); 
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
			else {
				ABTNode nextChild = steps.get(stepsCompleted);
				nextChild.setParent(this);
				getChildren().add(nextChild);
			}
		}
	}
}
