package abllite.abt;


public class SpawnGoalNode extends GoalNode {

	public SpawnGoalNode(String goalName, Object[] parameters) {
		super(goalName, parameters);
	}

	public String toString() { 
		return "SpawnGoalNode: " + getGoalName() + " [" + getParameters().length + "] (" + nodeStatus + ") " + getPriority(); 
	}
}
