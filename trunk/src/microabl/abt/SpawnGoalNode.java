package microabl.abt;
/**
 * Represents a goal to accomplish. 
 * 
 * Spawned goals are added to the root of the ABT. 
 */
public class SpawnGoalNode extends GoalNode {
   
	/**
	 * Creates a root goal node for the specified goal and parameters. 
	 */
	public SpawnGoalNode(String goalName, Object[] parameters) {
		super(goalName, parameters);
	}
	  
	/**
	 * Since spawned goals do not have parent behaviors, it is necessary to bind goal parameters when the step is expanded. 
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
 
	public String toString() { 
		return "SpawnGoalNode: " + getGoalName() + " [" + getParameters().length + "] (" + nodeStatus + ") " + getPriority(); 
	}
}
