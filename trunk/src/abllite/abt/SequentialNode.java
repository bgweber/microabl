package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;

public class SequentialNode extends BehaviorNode {

	public SequentialNode(BehaviorPrototype prototype, HashMap<String, Object> parameters) {
		super(prototype, parameters);
	}
	 
	 
	public String toString() {
		return "SequentialNode: " + goalName + " (" + nodeStatus + ") " + getPriority(); 
	} 
	 
	public void childCompleted(ABTNode child) {
		System.out.println("Child completed: " + child);
		
		if (child.isFailure()) {
			setStatus(NodeStatus.Failure);
		} 
		else {
			steps.remove(child);
			
			System.out.println("Steps: " + steps.size());
 			
			if (steps.size() == 0) {
				setStatus(NodeStatus.Success);
			} 
			else {
				setStatus(NodeStatus.Open);
			}
		}
		
		System.out.println("States: " + nodeStatus);
	}
}
