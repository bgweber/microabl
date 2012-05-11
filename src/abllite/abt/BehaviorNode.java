package abllite.abt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import abllite.prototype.BehaviorPrototype;
import abllite.prototype.ConditionPrototype;
import abllite.prototype.StepPrototype;

public class BehaviorNode extends ABTNode {

	private String goalName; 
 
	protected ArrayList<ABTNode> steps = new ArrayList<ABTNode>();
  	 
	private HashMap<String, Object> variables = new HashMap<String, Object>(); 

	private ArrayList<ConditionPrototype> contextConditions = new ArrayList<ConditionPrototype>();
	private ArrayList<ConditionPrototype> successConditions = new ArrayList<ConditionPrototype>();

	public BehaviorNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		this.goalName = prototype.getGoalName();
		this.variables = variables;
		
		this.contextConditions = prototype.getContextConditions();
		this.successConditions = prototype.getsuccessConditions(); 
  
		for (StepPrototype stepPrototype : prototype.getSteps()) {
			steps.add(stepPrototype.createABTNode(false));			
		}
	} 

	public ArrayList<ConditionPrototype> getContextConditions() {
		return contextConditions;
	}
  
	public ArrayList<ConditionPrototype> getSuccessConditions() {
		return successConditions;
	}
	
	public String getGoalName() {
		return goalName;
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
	 
	public HashMap<String, Object> getVariables() {
		return variables;
	}
}
