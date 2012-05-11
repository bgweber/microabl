package abllite.abt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import abllite.prototype.BehaviorPrototype;
import abllite.prototype.StepPrototype;

public class BehaviorNode extends ABTNode {

	// context conditions, success conditions 
 
	protected String goalName; 
 
	protected ArrayList<ABTNode> steps = new ArrayList<ABTNode>();
  	 
	private HashMap<String, Object> variables = new HashMap<String, Object>(); 
	
	public BehaviorNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		this.goalName = prototype.getGoalName();
		this.variables = variables;
  
		for (StepPrototype stepPrototype : prototype.getSteps()) {
			steps.add(stepPrototype.createABTNode(false));			
		}
	} 
	 
	public ArrayList<ABTNode> getSteps() {
		return steps;
	}
	 
	public Object getVariable(String name) {
		
		if (!variables.containsKey(name)) { 
			throw new ABTRuntimeError("Unbound variable: " + name + " in behavior: " + goalName);  
		}
		
		return variables.get(name);
	}
 	    
	public void setVariables(Map<String, Object> variables) {
		this.variables.putAll(variables);
	} 
}
