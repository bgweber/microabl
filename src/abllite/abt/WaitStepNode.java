package abllite.abt;

import java.util.ArrayList;

import abllite.prototype.ConditionPrototype;

public class WaitStepNode extends ABTNode {

	private ArrayList<ConditionPrototype> waitConditions;

	public WaitStepNode(ArrayList<ConditionPrototype> waitConditions) {
		this.waitConditions = waitConditions;
	}
  	 
	public ArrayList<ConditionPrototype> getWaitConditions() {
		return waitConditions;
	}
	
	public String toString() {
		return "WaitStepNode (" + nodeStatus + ") " + getPriority();  
	}
}
 