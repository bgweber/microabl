package abllite.abt;

import java.util.ArrayList;

import abllite.prototype.BehaviorPrototype;
import abllite.prototype.StepPrototype;

public class BehaviorNode extends ABTNode {

	// context conditions, success conditions 

	protected String goalName; 
 
	protected ArrayList<ABTNode> steps = new ArrayList<ABTNode>();
	
	public BehaviorNode(BehaviorPrototype prototype) {
		this.goalName = prototype.getGoalName();
 
		for (StepPrototype stepPrototype : prototype.getSteps()) {
			steps.add(stepPrototype.createABTNode(false));			
		}
	} 
	 
	public ArrayList<ABTNode> getSteps() {
		return steps;
	}
}
