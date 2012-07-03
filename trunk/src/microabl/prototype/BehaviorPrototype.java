package microabl.prototype;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * Represents an ABL behavior. These are prototype behaviors used to define the 
 * behavior library and are not added to the ABT. 
 * 
 * Behavior parameters are bound to behavior-scoped variables. 
 * 
 * The following behavior types are supported:
 *   Sequential - steps are added to the ABT one at a time.
 *   Parallel  - steps are added to the ABT all at once and pursued concurrently. 
 */
public class BehaviorPrototype { 

	/** behavior types */
	public enum BehaviorType { Sequential, Parallel }	 

	/** the behavior type */ 
	private BehaviorType behaviorType; 	

	/** the name of the goal the behavior accomplishes */ 
	private String goalName; 

	/** child steps contained in this behavior */ 
	private ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>();	

	/** classes of parameters in the behavior signiture */ 
	private ArrayList<Class> parameterClasses = new ArrayList<Class>(); 
	
	/** names of parameters in the behavior signiture */ 
	private ArrayList<String> parameterNames = new ArrayList<String>(); 
  
	/** conditions that must be true for the behavior to be selected for expansion */
	private ArrayList<ConditionPrototype> preconditions = new ArrayList<ConditionPrototype>(); 

	/** conditions that must remain true during the duration of the behavior */ 
	private ArrayList<ConditionPrototype> contextConditions = new ArrayList<ConditionPrototype>(); 
	
	/** conditions that cause the behavior to immediately succeed */ 
	private ArrayList<ConditionPrototype> successConditions = new ArrayList<ConditionPrototype>(); 

	/** the specificity of the behavior (higher specificity is selected first) */ 
	private int specificity = 0; 
	
	/** number of child steps needed for success in a parallel behavior, defaults to all steps */ 
	private int numberNeededForSuccess = 0;   

    private BehaviorPrototype(BehaviorType behaviorType, String goalName) {
    	this.behaviorType = behaviorType;
    	this.goalName = goalName; 
    }

    /**
     * Instantiates a sequential behavior. 
     * 
     * @param goalName - the goal the behavior accomplishes. 
     */
    public static BehaviorPrototype createSequential(String goalName) {
    	return new BehaviorPrototype(BehaviorType.Sequential, goalName);
    }
 
    /**
     * Instantiates a parallel behavior. 
     * 
     * @param goalName - the goal the behavior accomplishes. 
     */
    public static BehaviorPrototype createParallel(String goalName) {
    	return new BehaviorPrototype(BehaviorType.Parallel, goalName);
    }
     
    public BehaviorPrototype setSteps(ArrayList<StepPrototype> steps) {
    	this.steps = steps;
    	return this; 
    }
 
    public BehaviorPrototype setNumberNeededForSuccess(int numberNeededForSuccess) {
    	this.numberNeededForSuccess = numberNeededForSuccess;
    	return this; 
    }

    /**  
     * Adds a parameter to the behavior signature. 
     */
    public BehaviorPrototype addParameter(Class type, String name) {
    	parameterClasses.add(type);
    	parameterNames.add(name); 
    	return this; 
    }

    public BehaviorPrototype setPreconditions(ArrayList<ConditionPrototype> preconditions) {
    	this.preconditions = preconditions;
    	return this; 
    }

    public BehaviorPrototype setContextConditions(ArrayList<ConditionPrototype> contextConditions) {
    	this.contextConditions = contextConditions;
    	return this; 
    }
  
    public BehaviorPrototype setSuccessConditions(ArrayList<ConditionPrototype> successConditions) {
    	this.successConditions = successConditions;
    	return this;   
    }

    public BehaviorPrototype setSpecificity(int specificity) {
    	this.specificity = specificity;
    	return this; 
    }

    /**
     * Returns true if the behavior matches the given goal name and goal parameters. 
     * 
     * @param goalName - the name of the goal to accomplish. 
     * @param parameters - parameters passed to the goal. 
     * @return true if this behavior can accomplish the goal.  
     */
	public boolean matchingSignature(String goalName, Object[] parameters) {
		if (!this.goalName.equals(goalName)) {
			return false;
		}
				
		if (parameters.length != parameterClasses.size()) {
			return false;
		}

		for (int index=0; index<parameters.length; index++) { 
			if (!parameterClasses.get(index).isInstance(parameters[index])) {
				return false; 
			}
		}
		
		return true;
	}
    
	/**
	 * Given a set of goal parameters, this method binds the parameters to the parameter names
	 * in the behavior signature.
	 *  
	 * @param parameters - the goal parameters. 
	 * @return a mapping of behavior variables to goal parameter values. 
	 */
	public HashMap<String, Object> bindVariables(Object[] parameters) {
		HashMap<String, Object> variables = new HashMap<String, Object>();
 
		int index = 0;
		for (Object parameter : parameters) { 
			variables.put(parameterNames.get(index), parameter);
			index++;
		}
  
		return variables;
	}
    
    public int getNumberNeededForSuccess() {
    	return numberNeededForSuccess;
    }

	public String getGoalName() {
		return goalName;
	}	
	 
	public ArrayList<StepPrototype> getSteps() {
		return steps; 
	}
 
	public int getSpecificity() {
		return specificity;
	}
	
	public boolean isSequential() {
		return behaviorType == BehaviorType.Sequential;
	}
	  
	public boolean isParallel() {
		return behaviorType == BehaviorType.Parallel;
	}
	 
	public ArrayList<ConditionPrototype> getPreconditions() {
		return preconditions;
	}
	
	public ArrayList<ConditionPrototype> getContextConditions() {
		return contextConditions;
	}
	
	public ArrayList<ConditionPrototype> getsuccessConditions() {
		return successConditions;
	} 
}
 