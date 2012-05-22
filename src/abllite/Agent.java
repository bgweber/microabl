package abllite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import abllite.abt.ABTNode;
import abllite.abt.ABTRuntimeError;
import abllite.abt.ActionNode;
import abllite.abt.BehaviorNode;
import abllite.abt.GoalNode;
import abllite.abt.MentalActNode;
import abllite.abt.ModifierNode;
import abllite.abt.ParallelNode;
import abllite.abt.SequentialNode;
import abllite.abt.SpawnGoalNode;
import abllite.abt.WaitStepNode;
import abllite.abt.ABTNode.NodeStatus;
import abllite.action.ActionListener;
import abllite.prototype.BehaviorPrototype;
import abllite.prototype.ConditionPrototype;
import abllite.prototype.Variable;
import abllite.wm.WME;
import abllite.wm.WorkingMemory;
/**
 * Represents an ABL agent. An agent has a working memory, behavior library, and collection
 * of action behavior trees (ABT). 
 *
 * An ABL agent pursues a collection of goals, manage by the ABT. Each decision cycle (update)
 * the agent expands behaviors that pursue open goals. Each behavior contains a set of steps
 * that perform actions that work towards accomplishing the goal. 
 */
public class Agent {

	/** behavior prototypes available for expansion */ 
	private ArrayList<BehaviorPrototype> behaviorLibrary; 
	
	/** action listener for executing physical actions */ 
	private ActionListener actionListener; 
 
	/** the agent's working memory */ 
	private WorkingMemory workingMemory = new WorkingMemory();

	/** list of BTs monitored by the agent */ 
	private ArrayList<ABTNode> rootNodes = new ArrayList<ABTNode>();  
	
	/** the agent's initial goal */ 
	public static String INITIAL_GOAL = "init_tree"; 

	/** comparator for sorting ABT nodes based on priority */ 
	Comparator<ABTNode> priorityComparator = new Comparator<ABTNode>() {
		public int compare(ABTNode node1, ABTNode node2) { 
			if (node1.getPriority() != node2.getPriority()) {
				return node2.getPriority() - node1.getPriority();
			}
			else {
				return node1.hashCode() - node2.hashCode();
			}
		} 
	}; 
 	
	/** comparator for sorting behavior prototypes based on specificity */ 
	Comparator<BehaviorPrototype> specificityComparator = new Comparator<BehaviorPrototype>() {
		public int compare(BehaviorPrototype b1, BehaviorPrototype b2) {
			if (b1.getSpecificity() != b2.getSpecificity()) {
				return b2.getSpecificity() - b1.getSpecificity();
			} 
			else {
				return b1.hashCode() - b2.hashCode();
			}
		}			
	};
		
	/**
	 * Creates an agent with the given behavior library and action listener. The agent creates an ABT with 
	 * a single goal. 

	 * @param behaviorLibrary - behaviors available for expansion
	 * @param actionListener - action listener for performing physical actions 
	 */
	public Agent(ArrayList<BehaviorPrototype> behaviorLibrary, ActionListener actionListener) {
		this.behaviorLibrary = behaviorLibrary;
		this.actionListener = actionListener;		
  		  
		rootNodes.add(new GoalNode(INITIAL_GOAL, new Object[0]));
	}

	/**
	 * Returns the agent's working memory. 
	 */
	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	} 
 	 
	/**
	 * Performs an ABL decision cycle, or tick. The following tasks are performed 
	 * during a decision cycle:
	 *  1. Success and context conditions are tested. 
	 *  2. Subtrees attached to completed nodes are pruned.
	 *  3. Completed nodes are removed from the ABT. 
	 *  4. Open nodes are retrieved.
	 *  5. The highest priority open node is expanded. 
	 *  
	 * @return true if the ABT was modified 
	 */
	public boolean update() {
		boolean treeModified = false; 
		
		// tree finished?
		if (rootNodes.size() == 0) {
			return treeModified; 
		} 
  		 
		// 1. test success conditions and context conditions
		for (ABTNode root : rootNodes) {
			testBehaviorCondtions(root);
		}
 
		// 2. clear subtrees attached to completed parents 
		for (ABTNode root : rootNodes) {
			pruneTree(root);
		}
		 
		// 3. find completed nodes
		ArrayList<ABTNode> completed = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findCompletedNodes(root, completed);
		}
  
		for (ABTNode node : completed) {
			treeModified = true;
			ABTNode parent = node.getParent();
			
			if (parent == null) {
				rootNodes.remove(node);
 				 
				if (rootNodes.size() == 0) {
					return treeModified; 
				}
			}  
			else {  
				parent.removeChild(node);
				parent.childCompleted(node);
			}
		}

		// 4. find open nodes
		ArrayList<ABTNode> open = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findOpenNodes(root, open);
		} 
		
		// 5. expand open nodes
		Collections.sort(open, priorityComparator);
		
		for (ABTNode node : open) {
			if (expand(node)) {
				return true;
			}
		}
		
		// no new nodes were expanded 
		return treeModified; 
	}
 
	/**
	 * Evaluates the success conditions and context conditions of executing behaviors. 
	 */
	private void testBehaviorCondtions(ABTNode node) {
		if (node instanceof BehaviorNode && node.isExecuting()) {
			BehaviorNode behavior = (BehaviorNode)node;
			 
			// Behaviors with satisfied success conditions immediately succeed.
			if (behavior.getSuccessConditions().size() > 0) {
				if (checkConditions(behavior.getVariables(), behavior.getSuccessConditions(), 0)) {
					behavior.setStatus(NodeStatus.Success);
				}
			}
 			 
			// Behaviors with unsatisfied context conditions immediately fail.
			if (behavior.getContextConditions().size() > 0) {
				if (!checkConditions(behavior.getVariables(), behavior.getContextConditions(), 0)) {
					behavior.setStatus(NodeStatus.Failure);
				}
			}
		}

		// iterate through child nodes 
		for (ABTNode child : node.getChildren()) {
			testBehaviorCondtions(child);
		}			
	}

	/**
	 * Searches for completed nodes with active children. Children of completed
	 * nodes are aborted. 
	 */
	private void pruneTree(ABTNode node) {
		if (node.isCompleted() && node.getNumChildren() > 0) {

			for (ABTNode child : node.getChildren()) {
				abortTree(child);
			}
			
			node.clearChildren();
		}
		
		// iterate through child nodes 
		for (ABTNode child : node.getChildren()) {
			pruneTree(child);
		}
	} 
 
	/** 
	 * Tells the node to abort all children. 
	 * 
	 * If the node is an action, then the action is aborted. 
	 */
	private void abortTree(ABTNode node) {
		for (ABTNode child : node.getChildren()) {
			abortTree(child);
		} 
		
		if (node instanceof ActionNode) {
			this.actionListener.abort((ActionNode)node); 
		}
	}

	/**
	 * Finds completed nodes in the ABT. 
	 */
	private void findCompletedNodes(ABTNode node, ArrayList<ABTNode> completed) {

		if (node.getNumChildren() > 0) {
			for (ABTNode child : node.getChildren()) {
				findCompletedNodes(child, completed);
			}			
		}
		else if (node.isCompleted()) {
			completed.add(node);
		}
	}  
	  
	/**
	 * Finds open nodes in the ABT. 
	 */
	private void findOpenNodes(ABTNode node, ArrayList<ABTNode> available) {
		if (node.isSuccess() || node.isFailure()) {
			return;
		}
		
		if (node.isOpen()) {
			available.add(node);
		}
		
		for (ABTNode child : node.getChildren()) {
			findOpenNodes(child, available);
		}
	}

	/**
	 * Sets a variable in the node's enclosing behavior. 
	 * 
	 * Throws an error if the node has no enclosing behavior. 
	 */
	private void setVariable(ABTNode node, String name, Object value) {
	
 		// get the parent behavior 
		while (node != null && !(node instanceof BehaviorNode)) {
			node = node.getParent();
		}
		
		if (node == null) {
			throw new ABTRuntimeError("Step has no parent behavior");  
		}
 	 	 
		((BehaviorNode)node).setVariable(name, value);
	}
 
	/**
	 * Gets a variable value from the node's enclosing behavior. 
	 * 
	 * Throws an error if the node has no enclosing behavior. 
	 */
	private Object getVariable(ABTNode node, String name) {

 		// get the parent behavior 
		while (node != null && !(node instanceof BehaviorNode)) {
			node = node.getParent();
		}

		if (node == null) {
			throw new ABTRuntimeError("Step has no parent behavior");  
		}
		 
		return ((BehaviorNode)node).getVariable(name);
	}

	/**
	 * Binds parameters to behavior-scoped variables. 
	 * 
	 * Before binding, parameters are specified as literals and Variable objects. During binding, 
	 * Variable objects are replaced with the value of the variable in the enclosing behavior.  
	 */
	private Object[] bindVariables(ABTNode node, Object[] parameters) {
		Object[] executionParameters = new Object[parameters.length];
 		 
		for (int index=0; index<parameters.length; index++) {
			executionParameters[index] = (parameters[index] instanceof Variable) ? 
				getVariable(node, (((Variable)parameters[index]).getName())) : parameters[index];
		}
  
		return executionParameters;
	}
  
	/**
	 * Expands the ABT node. 
	 * 
	 * @return true if the node was expanded 
	 */
	private boolean expand(ABTNode node) {
		
		if (node instanceof GoalNode) {
			return expandGoal((GoalNode)node); 
		}
		else if (node instanceof ParallelNode) {
			return expandParallel((ParallelNode)node); 
		}
		else if (node instanceof SequentialNode) {
			return expandSequential((SequentialNode)node); 
		}
		else if (node instanceof ActionNode) {
			return expandAction((ActionNode)node); 
		}
		else if (node instanceof ModifierNode) {
			return expandModifier((ModifierNode)node);  
		} 
		else if (node instanceof WaitStepNode) {
			return expandWaitStep((WaitStepNode)node);  
		} 
		else if (node instanceof MentalActNode) {
			return expandMentalAct((MentalActNode)node);  
		} 
		else {
			throw new ABTRuntimeError("Invalid ABT node type");  
		}
	} 

	/**
	 * Expands a mental act by binding the method parameters and invoking the method. If a result
	 * parameter is specified, then the result is bound to a behavior variable. 
	 */
	private boolean expandMentalAct(MentalActNode mentalAct) {
		
		mentalAct.execute(bindVariables(mentalAct, mentalAct.getPrototypeParameters()));

		if (mentalAct.getResultBinding() != null) {
			setVariable(mentalAct, mentalAct.getResultBinding(), mentalAct.getResult()); 			
		}

		return true; 
	}

	/**
	 * Expands a wait step by checking the wait conditions. If the conditions are met then the step success,
	 * otherwise the step remains open. 
	 */
	private boolean expandWaitStep(WaitStepNode waitStep) {
 
		if (checkConditions(((BehaviorNode)waitStep.getParent()).getVariables(), waitStep.getWaitConditions(), 0)) {
			waitStep.setStatus(NodeStatus.Success);
			return true; 
		}
		else {
			return false; 
		}
 	}

	/**
	 * Expands a modifier step by instantiating the step it wraps and adding the step to the ABT. 
	 */
	private boolean expandModifier(ModifierNode modifier) {
		modifier.setStatus(NodeStatus.Executing); 						
		
		ABTNode child = modifier.createABTNode();
		modifier.addChild(child);
		child.setParent(modifier); 
		child.setPriority(modifier.getPriority());
		
		return true;
	}

	/**
	 * Expands an action by binding the action parameters and notifying the action listener. 
	 */
	private boolean expandAction(ActionNode action) {
 
		action.bindParameters(bindVariables(action, action.getPrototypeParameters())); 
		action.setStatus(NodeStatus.Executing); 						
		actionListener.execute(action);
		return true;
	}

	/**
	 * Expands a parallel behavior by adding all child steps to the ABT. 
	 */
	private boolean expandParallel(ParallelNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						

		if (behavior.getNumSteps() == 0) {
			throw new ABTRuntimeError("Behavior has no child steps: " + behavior.getGoalName());
		}

		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
		}
  		
		return true;
	}

	/**
	 * Expands a sequential behavior by adding the first child to the ABT. 
	 */
	private boolean expandSequential(SequentialNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						
		 
		if (behavior.getNumSteps() == 0) {
			throw new ABTRuntimeError("Behavior has no child steps: " + behavior.getGoalName());
		}
 
		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
			break;   
		}
 		
		return true;
	}
 
	/**
	 * Expands a behavior step by adding it to the ABT. 
	 * 
	 * Spawn goal steps are a special case. During execution, a new root ABT node is created. 
	 */
	private void expandBehaviorStep(BehaviorNode behavior, ABTNode step) {		

		// inherit priority 
		if (!step.getPrioritySpecified()) {
			step.setPriority(behavior.getPriority());
		}
  
		// if this is a spawn goal step, create a new ABT root node 
		if (step instanceof SpawnGoalNode) { 

			// bind the parameters now, since the spawned goal will not have a parent behavior 
			((SpawnGoalNode)step).setParameters(bindVariables(behavior, ((SpawnGoalNode)step).getParameters()));
 			
			rootNodes.add(step);
			behavior.childCompleted(step);
		}
		// add the step to the ABL 
		else {
			behavior.addChild(step);
			step.setParent(behavior); 			
		}
	}
 
	/**
	 * Expands a goal node. 
	 * 
	 * Expanding a goal involves the following tasks:
	 *  1. Bind goal parameters to behavior variables
	 *  2. Find behaviors with matching signature.
	 *  3. Sort matching behaviors by specificity 
	 *  4. Attempt to expand behaviors
	 */
	private boolean expandGoal(GoalNode goal) {

		// 1. bind goal parameters (note: spawngoal variables will already be bound) 
		Object[] goalParameters = bindVariables(goal, goal.getParameters());

		// 2. find matching behaviors in the library 
		ArrayList<BehaviorPrototype> matching = new ArrayList<BehaviorPrototype>();
		for (BehaviorPrototype prototype : behaviorLibrary) {
			if (prototype.matchingSignature(goal.getGoalName(), goalParameters)) {

				// dont retry behaviors for the same goal 
				if (!goal.hasAttempted(prototype)) {
					matching.add(prototype);
				}
			}  
		}
  
		// 3. sort by specificity  
		Collections.sort(matching, specificityComparator); 
  
		// 4. expand the first matching behavior 
		for (BehaviorPrototype prototype : matching) {
 
			// match goal parameters to behavior variables 
			HashMap<String, Object> variables = prototype.bindVariables(goalParameters); 
			 
			// check preconditions
			if (!checkConditions(variables, prototype.getPreconditions(), 0)) {
				continue; 
			}
   
			// expand the behavior 
			BehaviorNode behavior = prototype.isSequential() ? new SequentialNode(prototype, variables) : new ParallelNode(prototype, variables);
			behavior.setParent(goal);
			goal.addChild(behavior); 
			goal.attemptingBehavior(prototype);

			behavior.setPriority(goal.getPriority()); 
			behavior.setVariables(variables);			
			goal.setStatus(NodeStatus.Executing); 						
			return true;
		}  
		  
		// no matching behaviors found 
		goal.setStatus(NodeStatus.Failure); 						
		return false;
	}
  
	/**
	 * Evaluates a list of conditions. There are 3 types of conditions: 
	 *  1. WME conditions check for the existence of a WME that matches a set of tests. These conditions can
	 *     also specify bindings and assign retrieved WMEs to a behavior variable.
	 *  2. Negation conditions that check for the lack of a WME with a set of tests. No bindings are possible.
	 *  3. Mental conditions that invoke a Java method. The method must return a boolean and no bindings are possible. 
	 * 
	 * This method is recursive, because different WME bindings can be evaluated while testing conditions. 
	 * 
	 * @param variables - variables defined in the enclosing behavior 
	 * @param conditions - list of conditions to evaluate
	 * @param index - the index of the condition to test 
	 * @return true if all conditions evaluate to true 
	 */  
	private boolean checkConditions(HashMap<String, Object> variables, ArrayList<ConditionPrototype> conditions, int index) { 
  
		// all conditions are satisfied 
		if (index == conditions.size()) {
			return true;
		} 

		// retrieve the current condition to evaluate. 
		ConditionPrototype condition = conditions.get(index);
 
		// check for the existence of a WME 
		if (condition.isWMECheck()) {
 			  
			HashSet<WME> wmes = workingMemory.getWMEs(condition.getWMEClass());
			for (WME wme : wmes) {

				// check if the wme conditions match 
				if (!condition.testWME(wme, variables)) {
					continue; 
				}
				
				// bind properties
				HashMap<String, String> bindings = condition.getBindings();
				for (String attribute : bindings.keySet()) {
					variables.put(bindings.get(attribute), wme.getAttribute(attribute));
				} 
  
				// bind wme instance to a property
				if (condition.getWMEVariable() != null) {
					variables.put(condition.getWMEVariable(), wme);
				} 
 				 
				// recurse!!! 
				if (checkConditions(variables, conditions, index + 1)) {
					return true;
				}					
			}
			  
			// no valid WMEs found 
			return false; 
		}
		// check for lack of a WME 
		else if (condition.isNegationCheck()) {

			HashSet<WME> wmes = workingMemory.getWMEs(condition.getWMEClass());
			for (WME wme : wmes) {
				  
				// fail if conditions match 
				if (condition.testWME(wme, variables)) {
					return false; 
				}
			}
  
			// recurse!!! 
			return checkConditions(variables, conditions, index + 1);
		}  
		// mental condition 
		else {
			
			// execute the mental condition 
			if (condition.execute(variables)) {
				return checkConditions(variables, conditions, index + 1);
			}
			else {
				return false; 
			}
		}
	}

	/**
	 * Prints the stats of the ABT. 
	 */
	public void printABT() {
		if (rootNodes.size() > 0) {
			for (ABTNode root : rootNodes) {
				print(root, 0);
			} 
		}
		else {
			System.out.println("Tree is empty");
		}
	}	
	
	private void print(ABTNode node, int depth) {
		for (int i=0; i<depth; i++) System.out.print("  ");
		
		System.out.println("- " + node.toString());
		
		for (ABTNode child : node.getChildren()) {
			print(child, depth + 1);
		}
	}
}
 