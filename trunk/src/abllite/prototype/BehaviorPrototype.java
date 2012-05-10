package abllite.prototype;

import java.util.ArrayList;

public class BehaviorPrototype {

	public enum BehaviorType { Sequential, Parallel }	 
    
	private BehaviorType behaviorType; 	
	private String goalName; 
	private ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>();	
 
	private Class[] parameterClasses = new Class[0]; 
	private String[] parameterNames = new String[0]; 
  	 
	private int specificity; 
	  
	private int numberNeededForSuccess;  
	 
//	private ArrayList<Condition> preconditions; 
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

	public boolean matchingSignature(String goalName, Object[] parameters) {
		if (!this.goalName.equals(goalName)) {
			return false;
		}
		
		if (parameters.length != parameterClasses.length) {
			return false;
		}
		
		for (int i=0; i<parameters.length; i++) {
			if (parameterClasses[i].isInstance(parameters[i])) {
				return false; 
			}
		}
		
		return true;
	}

	public boolean isSequential() {
		return behaviorType == BehaviorType.Sequential;
	}
	  
	public boolean isParallel() {
		return behaviorType == BehaviorType.Parallel;
	}
}
 