package microabl.abt;

import java.util.ArrayList;

import microabl.prototype.ConditionPrototype;
/**
 * A node that suspends execution until a set of conditions becomes true. 
 */
public class WaitStepNode extends ABTNode {

	/** conditions to wait on */ 
	private ArrayList<ConditionPrototype> waitConditions;

	/**
	 * Instantiates a wait node with the specified wait conditions. 
	 */
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
  