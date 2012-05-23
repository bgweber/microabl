package microabl.abt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import microabl.prototype.BehaviorPrototype;
import microabl.prototype.ConditionPrototype;
import microabl.prototype.StepPrototype;
/**
 * Represents a behavior selected for expansion. 
 * 
 * If the status of a behavior is set to open, then there are child steps that
 * have not yet been added to the ABT. If the status is executing, then the behavior
 * is suspended until a child step completed. 
 */
public class BehaviorNode extends ABTNode {

	/** goal name the behavior accomplishes */ 
	private String goalName; 
 
	/** steps in the behavior */ 
	protected ArrayList<ABTNode> steps = new ArrayList<ABTNode>();
  	 
	/** behavior scoped variables */ 
	private HashMap<String, Object> variables = new HashMap<String, Object>(); 

	/** conditions that must remain true during execution */
	private ArrayList<ConditionPrototype> contextConditions = new ArrayList<ConditionPrototype>();

	/** conditions that cause the behavior to succeed imediately */
	private ArrayList<ConditionPrototype> successConditions = new ArrayList<ConditionPrototype>();

	/**
	 * Instantiates a behavior node from the given prototype 
	 * 
	 * Creates ABT nodes for child steps, but does not add the steps to the ABT. 
	 * 
	 * @param prototype - behavior prototype that specifies steps and conditions 
	 * @param variables - parameters for the goal the behavior accomplishes. 
	 */
	public BehaviorNode(BehaviorPrototype prototype, HashMap<String, Object> variables) {
		this.goalName = prototype.getGoalName();
		this.variables = variables;
 		
		this.contextConditions = prototype.getContextConditions();
		this.successConditions = prototype.getsuccessConditions(); 
   
		// create ABT nodes for each step in the behavior 
		for (StepPrototype stepPrototype : prototype.getSteps()) {
			steps.add(stepPrototype.createABTStep());			
		} 
	} 

	/**
	 * Returns the value of a behavior-scoped variable. 
	 * 
	 * Throws an error if the variable is not boundd. 
	 */
	public Object getVariable(String name) {
		
		if (!variables.containsKey(name)) { 
			throw new ABTRuntimeError("Unbound variable: " + name + " in behavior: " + goalName);  
		}
		
		return variables.get(name);
	}

	public void setVariable(String name, Object value) {
		variables.put(name, value); 
	}
  	    
	public void setVariables(Map<String, Object> variables) {
		this.variables.putAll(variables);
	} 
	 
	public HashMap<String, Object> getVariables() {
		return variables;
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

	public int getNumSteps() {
		return steps.size();
	}

	public Iterable<ABTNode> getSteps() {
		return steps;
	}	 
}
 