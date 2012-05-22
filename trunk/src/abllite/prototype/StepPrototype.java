package abllite.prototype;

import java.util.ArrayList;

import abllite.abt.ABTNode;
import abllite.abt.ABTRuntimeError;
import abllite.abt.ActionNode;
import abllite.abt.FailStepNode;
import abllite.abt.GoalNode;
import abllite.abt.MentalActNode;
import abllite.abt.ModifierNode;
import abllite.abt.SpawnGoalNode;
import abllite.abt.SucceedStepNode;
import abllite.abt.WaitStepNode;
/**
 * Represents a behavior step. These are prototype steps used to define the 
 * behavior library and are not added to the ABT. Priorities and modifiers can 
 * be assign to all step types. 
 * 
 * The following step types are provided:
 * 	Action - performs a physcal action
 *    stepName: name of the physical action
 *    parameters: action parameters, specified as literals and Variables
 *    
 *  Subgoal - creates a new sub goal with the specified parameters
 *    stepName: name of the sub goal
 *    parameters: goal parameters, specified as literals and Variables
 *    
 *  Spawngoal - creates a new root goal with the specified parameters 
 *    stepName: name of the new root goal 
 *    parameters: goal parameters, specified as literals and Variables
 *    
 *  WaitStep - suspended until wait conditions are met 
 *    waitConditions: conditions to wait on 
 *    
 *  FailStep - immediately returns failure 
 *  
 *  SucceedStep - immediately returns success 
 *  
 *  MentalAct - invokes a Java method (blocks the execution of the ABT) 
 *    actionClass: name of the class that contains the method 
 *    stepName: name of the static method to invoke
 *    parameters: method parameters, specified as literals and Variables
 *    resultBinding: an optional parameter for binding the result to a behavior-scoped variable 
 *     
 * The following modifiers are supported:
 *   None - immediately returns the status of the child 
 *   Persistent - continues to perform the step regardless of step success or failure
 *   IgnoreFailure - returns success when the step fails or succeeds 
 *   PersistentWhenSucceeds - continues to perform the step when it succeeds 
 *   PersistentWhenFails  - continues to perform the step when it fails  
 */
public class StepPrototype {
	
	/** step types (see definitions above) */
	public enum StepType { Action, Subgoal, Spawngoal, WaitStep, FailStep, SucceedStep, MentalAct }

	/** step modifiers (see definitions above) */
	public enum StepModifier { None, Persistent, IgnoreFailure, PersistentWhenSucceeds, PersistentWhenFails } // Effect Only

	/** the step type */ 
	private StepType stepType; 

	/** the step name */ 
	private String stepName; 	// action name, subgoal name, mental act method name
    
	/** the step parameters (for goals, actions, and mental acts */ 
	private Object[] parameters = new Object[0]; // considered literals, except for Variable instances 

	/** an optional modifier */
 	private StepModifier modifier = StepModifier.None; 

 	/** priority of the step (by default, inherits parent priority) */ 
	private int priority = 0; 

	/** set to true is the step priority should be set (do not inherit priority). */ 
	private boolean prioritySpecified = false;

	/** conditions that suspend the step (only for the wait step type) */ 
	private ArrayList<ConditionPrototype> waitConditions = new ArrayList<ConditionPrototype>();

	/** name of the class to perform mental act (only for the mental act step type) */ 
	private Class actionClass; 

	/** name of variable to bind after performing a mental act (only for the mental act step type) */ 
	private String resultBinding;  

	private StepPrototype(StepType stepType) {
		this.stepType = stepType;
	} 
 
	private StepPrototype(StepType stepType, String stepName) {
		this(stepType);
		this.stepName = stepName;
	}  

	private StepPrototype(StepType stepType, Class actionName, String stepName) {
		this(stepType, stepName);
		this.actionClass = actionName; 
	}

	/**
	 * Instantiates a subgoal step.
	 * 
	 * @param goalName - name of the subgoal 
	 */
	public static StepPrototype createSubgoal(String goalName) {
		return new StepPrototype(StepType.Subgoal, goalName);
	}
 
	/**
	 * Instantiates a spawngoal step.
	 * 
	 * @param goalName - name of the goal to spawn
	 */
	public static StepPrototype createSpawngoal(String goalName) {
		return new StepPrototype(StepType.Spawngoal, goalName);
	}

	/**
	 * Instantiates an action.
	 * 
	 * @param actionName - the physcial action to execute
	 */
	public static StepPrototype createAction(String actionName) {
		return new StepPrototype(StepType.Action, actionName);
	}

	/**
	 * Instantiates a mental act. 
	 * 
	 * @param actionClass - class to invoke
	 * @param methodName - method to invoke 
	 */
	public static StepPrototype createMentalAct(Class actionClass, String methodName) {
		return new StepPrototype(StepType.MentalAct, actionClass, methodName);
	}

	/**
	 * Instantiates a wait step. 
	 * 
	 * @param conditions - wait conditions
	 */
	public static StepPrototype createWaitStep(ArrayList<ConditionPrototype> conditions) {
		StepPrototype step = new StepPrototype(StepType.WaitStep);
		step.waitConditions = conditions; 
		return step;
	}

	/**
	 * Instantiates a success step. 
	 */
	public static StepPrototype createSucceedStep() {
		return new StepPrototype(StepType.SucceedStep);
 	}
 
	/**
	 * Instantiates a fail step. 
	 */
	public static StepPrototype createFailStep() {
		return new StepPrototype(StepType.FailStep);
	}

	public StepPrototype setParameters(Object[] parameters) {
		this.parameters = parameters;
		return this;
	} 
	
	public StepPrototype setResultBinding(String resultBinding) {
		this.resultBinding = resultBinding;
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

	/**
	 * Creates an ABT node based this prototype step. If a modifier is specified, this 
	 * method will return a modifier step. 
	 */
	public ABTNode createABTStep() {
 
		if (modifier != StepModifier.None) {
			return new ModifierNode(this, modifier);
		}
		else { 
			return createABTNode();
		}
	}
		 
	/**
	 * Creates an ABT node based this prototype step, ignoring the modifier. 
	 * 
	 * Throws an error if the node type is not defined. 
	 */
	public ABTNode createABTNode() {
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
		case MentalAct:
			node = new MentalActNode(actionClass, stepName, parameters, resultBinding);
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
 