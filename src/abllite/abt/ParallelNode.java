package abllite.abt;

import java.util.HashMap;

import abllite.prototype.BehaviorPrototype;
/**
 * Represents a parallel behavior. 
 */
public class ParallelNode extends BehaviorNode {
 
	/** number of successfully completed child steps */
	private int stepsCompleted = 0;  

	/** number of successfully completed child steps necessary for behavior success */
	private int numberNeededForSuccess; 	
	
	/**
	 * Creates a parallel behavior node from the prototype behavior. 
	 * 
	 * @param variables - parameters for the goal the behavior accomplishes. 
	 */
	public ParallelNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		super(prototype, variables);				
		numberNeededForSuccess = prototype.getNumberNeededForSuccess() > 0 ? prototype.getNumberNeededForSuccess() : steps.size();
	} 
   
	/**
	 * If the step failed, then the behavior fails immediately. Otherwise if there are no further steps or the 
	 * number of steps needed for success is reached, then the behavior succeeds. 
	 */
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
	
	public String toString() {
		return "ParallelNode: " + getGoalName() + " (" + nodeStatus + ") " + getPriority(); 
	}
}
