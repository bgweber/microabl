package abllite.prototype;

import java.util.ArrayList;
import java.util.HashMap;

public class BehaviorPrototype { 

	public enum BehaviorType { Sequential, Parallel }	 
    
	private BehaviorType behaviorType; 	
	private String goalName; 
	private ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>();	

	private ArrayList<Class> parameterClasses = new ArrayList(); 
	private ArrayList<String> parameterNames = new ArrayList(); 
 
	private int specificity; 
	   
//	private int numberNeededForSuccess;   
	 
	private ArrayList<ConditionPrototype> preconditions = new ArrayList<ConditionPrototype>(); 
//	private ArrayList<Condition> contextConditions; 
//	private ArrayList<Condition> successConditions;  
 	
	 
    public BehaviorPrototype(BehaviorType behaviorType, String goalName) {
    	this.behaviorType = behaviorType;
    	this.goalName = goalName; 
    }
 
    public static BehaviorPrototype createSequential(String goalName) {
    	return new BehaviorPrototype(BehaviorType.Sequential, goalName);
    }

    public static BehaviorPrototype createParallel(String goalName) {
    	return new BehaviorPrototype(BehaviorType.Parallel, goalName);
    }
    
    public BehaviorPrototype setSteps(ArrayList<StepPrototype> steps) {
    	this.steps = steps;
    	return this; 
    }
 
    public BehaviorPrototype addParameter(Class type, String name) {
    	parameterClasses.add(type);
    	parameterNames.add(name); 
    	return this; 
    }

    public BehaviorPrototype setPreconditions(ArrayList<ConditionPrototype> preconditions) {
    	this.preconditions = preconditions;
    	return this; 
    }

    public BehaviorPrototype setSpecificity(int specificity) {
    	this.specificity = specificity;
    	return this; 
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
   
	public HashMap<String, Object> bindVariables(Object[] parameters) {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		int index = 0;
		for (Object parameter : parameters) { 
			variables.put(parameterNames.get(index), parameter);
			index++;
		}
  
		return variables;
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
}
 