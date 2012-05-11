package abllite.prototype;

import java.util.ArrayList;

import abllite.abt.ABTNode;
import abllite.abt.ABTRuntimeError;
import abllite.abt.ActionNode;
import abllite.abt.FailStepNode;
import abllite.abt.GoalNode;
import abllite.abt.ModifierNode;
import abllite.abt.SpawnGoalNode;
import abllite.abt.SucceedStepNode;
import abllite.abt.WaitStepNode;

public class StepPrototype {
 
	public enum StepType { Action, Subgoal, Spawngoal, WaitStep, FailStep, SucceedStep }
	public enum StepModifier { None, Persistent, IgnoreFailure, PersistentWhenSucceeds, PersistentWhenFails } // Effect Only

	private StepType stepType; 
	private String stepName; 	// action name, subgoal name
    
	private Object[] parameters = new Object[0]; // considered literals, except for Variable instances 
	private StepModifier modifier = StepModifier.None; 

	private int priority = 0; 
	private boolean prioritySpecified = false;

	private ArrayList<ConditionPrototype> waitConditions = new ArrayList<ConditionPrototype>();
	
	public StepPrototype(StepType stepType) {
		this.stepType = stepType;
	} 
 
	public StepPrototype(StepType stepType, String stepName) {
		this(stepType);
		this.stepName = stepName;
	} 
   
	public static StepPrototype createSubgoal(String goalName) {
		return new StepPrototype(StepType.Subgoal, goalName);
	}
 
	public static StepPrototype createSpawngoal(String goalName) {
		return new StepPrototype(StepType.Spawngoal, goalName);
	}

	public static StepPrototype createAction(String actionName) {
		return new StepPrototype(StepType.Action, actionName);
	}
 
	public static StepPrototype createWaitStep(ArrayList<ConditionPrototype> conditions) {
		StepPrototype step = new StepPrototype(StepType.WaitStep);
		step.waitConditions = conditions; 
		return step;
	}

	public static StepPrototype createSucceedStep() {
		return new StepPrototype(StepType.SucceedStep);
 	}
 
	public static StepPrototype createFailStep() {
		return new StepPrototype(StepType.FailStep);
	}

	public StepPrototype setParameters(Object[] parameters) {
		this.parameters = parameters;
		return this;
	} 
	 
	public StepPrototype setPriority(int priority) {
		this.priority = priority;
		prioritySpecified = true;
		return this;
	} 
	  
	public StepPrototype setModifier(StepModifier modifier) {
		this.modifier = modifier;
		return this;
	}

	public ArrayList<ConditionPrototype> getWaitConditions() {
		return waitConditions;
	}
	
	public StepType getStepType() {
		return stepType;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public String getStepName() {
		return stepName;
	}
	
	public Object[] getParameters() {
		return parameters;
	}
	
	public StepModifier getModifier() {
		return modifier; 
	}

	public ABTNode createABTNode(boolean ignoreModifier) {
 
		if (ignoreModifier == false && modifier != StepModifier.None) {
			return new ModifierNode(this, modifier);
		}
		else { 
			ABTNode node = null; 
			
			switch (stepType) {
			case Action:
				node = new ActionNode(stepName, parameters);
				break;
			case Subgoal:
				node = new GoalNode(stepName, parameters);
				break;
			case Spawngoal:
				node = new SpawnGoalNode(stepName, parameters);
				break;
			case WaitStep:
				node = new WaitStepNode(waitConditions);
				break; 
			case SucceedStep:
				node = new SucceedStepNode();
				break;
			case FailStep:
				node= new FailStepNode();
				break; 
			}			
			
			if (node != null) {
				if (prioritySpecified) {
					node.setPriority(priority);
				}
				
				return node;
			}
			else {
				throw new ABTRuntimeError("Undefined ABTNode type");
			}
		} 		
	}		
}
 