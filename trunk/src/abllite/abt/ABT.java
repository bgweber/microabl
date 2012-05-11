package abllite.abt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import abllite.abt.ABTNode.NodeStatus;
import abllite.action.ActionListener;
import abllite.prototype.BehaviorPrototype;
import abllite.prototype.ConditionPrototype;
import abllite.prototype.Variable;
import abllite.wm.WME;
import abllite.wm.WorkingMemory;

public class ABT {
	 
	private ArrayList<BehaviorPrototype> behaviorLibrary; 
	
	private ActionListener actionListener; 
 
	private WorkingMemory workingMemory;

	private ArrayList<ABTNode> rootNodes = new ArrayList<ABTNode>();  
	
	public static String INITIAL_GOAL = "init_tree"; 

	public ABT(ArrayList<BehaviorPrototype> behaviorLibrary, WorkingMemory workingMemory, ActionListener actionListener) {
		this.behaviorLibrary = behaviorLibrary;
		this.workingMemory = workingMemory;
		this.actionListener = actionListener;		
		  
		rootNodes.add(new GoalNode(INITIAL_GOAL, new Object[0]));
	}

	public boolean performDecisionCycle() {
		
		// tree finished?
		if (rootNodes.size() == 0) {
			return false; 
		}
 		 
		// test success conditions and context conditions
		for (ABTNode root : rootNodes) {
			testBehaviorCondtions(root);
		}
 
		// clear subtrees attached to completed parents 
		for (ABTNode root : rootNodes) {
			pruneTree(root);
		}
		 
		// find completed nodes
		ArrayList<ABTNode> completed = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findCompletedNodes(root, completed);
		}
 
		for (ABTNode node : completed) {
			ABTNode parent = node.getParent();
			
			if (parent == null) {
				rootNodes.remove(node);
 				 
				if (rootNodes.size() == 0) {
					return false; 
				}
			}  
			else {  
				parent.getChildren().remove(node);				
				parent.childCompleted(node);
			}
		}

		// find open nodes
		ArrayList<ABTNode> open = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findOpenNodes(root, open);
		} 
		
		// sort open nodes based on priority   
		Collections.sort(open, new Comparator<ABTNode>() {
			public int compare(ABTNode node1, ABTNode node2) { 
				if (node1.getPriority() != node2.getPriority()) {
					return node2.getPriority() - node1.getPriority();
				}
				else {
					return node1.hashCode() - node2.hashCode();
				}
			} 
		}); 
 
		// expand open nodes
		for (ABTNode node : open) {
			if (expand(node)) {
				return true;
			}
		}
		
		// no new nodes were expanded 
		return false; 
	}

	private void testBehaviorCondtions(ABTNode node) {
		if (node instanceof BehaviorNode && node.isExecuting()) {
			BehaviorNode behavior = (BehaviorNode)node;
			 
			if (behavior.getSuccessConditions().size() > 0) {
				if (checkConditions(behavior.getVariables(), behavior.getSuccessConditions(), 0)) {
					behavior.setStatus(NodeStatus.Success);
				}
			}
 			 
			if (behavior.getContextConditions().size() > 0) {
				if (!checkConditions(behavior.getVariables(), behavior.getContextConditions(), 0)) {
					behavior.setStatus(NodeStatus.Failure);
				}
			}
		}
		
		for (ABTNode child : node.getChildren()) {
			testBehaviorCondtions(child);
		}			
	}

	private void pruneTree(ABTNode node) {
		if (node.isCompleted() && node.getChildren().size() > 0) {

			for (ABTNode child : node.getChildren()) {
				abortTree(child);
			}
			
			node.clearChildren();
		}
		
		for (ABTNode child : node.getChildren()) {
			pruneTree(child);
		}
	} 
	 
	private void abortTree(ABTNode node) {
		for (ABTNode child : node.getChildren()) {
			abortTree(child);
		} 
		
		if (node instanceof ActionNode) {
			this.actionListener.abort((ActionNode)node); 
		}
	}

	private void findCompletedNodes(ABTNode node, ArrayList<ABTNode> completed) {

		if (node.getChildren().size() > 0) {
			for (ABTNode child : node.getChildren()) {
				findCompletedNodes(child, completed);
			}			
		}
		else if (node.isCompleted()) {
			completed.add(node);
		}
		
	}  
	  
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
	
	private Object getVariable(ABTNode node, String name) {

 		// get the parent behavior 
		while (node != null && !(node instanceof BehaviorNode)) {
			node = node.getParent();
		}

		if (node == null) {
			throw new ABTRuntimeError("Step has no parent behavior");  
		}
		 
		return (node != null) ? ((BehaviorNode)node).getVariable(name) : null;
	}
 
	private Object[] bindVariables(ABTNode node, Object[] parameters) {
		Object[] executionParameters = new Object[parameters.length];
 		 
		for (int index=0; index<parameters.length; index++) {
			executionParameters[index] = (parameters[index] instanceof Variable) ? getVariable(node, (((Variable)parameters[index]).getName())) : parameters[index];
		}
 
		return executionParameters;
	}
   
	private boolean expand(ABTNode node) {
//		System.out.println("Trying to expand: " + node);		
		
		// try to expand the goal
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
 
		return false; 
	}
	
	private boolean expandWaitStep(WaitStepNode waitStep) {
 
		if (checkConditions(((BehaviorNode)waitStep.getParent()).getVariables(), waitStep.getWaitConditions(), 0)) {
			waitStep.setStatus(NodeStatus.Success);
			return true; 
		}
		else {
			return false; 
		}
 	}

	private boolean expandModifier(ModifierNode modifier) {
		modifier.setStatus(NodeStatus.Executing); 						
		
		ABTNode child = modifier.createABTNode();
		modifier.addChild(child);
		child.setParent(modifier); 
		child.setPriority(modifier.getPriority());
		
		return true;
	}
	  
	private boolean expandAction(ActionNode action) {
 
		action.setParameters(bindVariables(action, action.getParameters())); 
		action.setStatus(NodeStatus.Executing); 						
		actionListener.execute(action);
		return true;
	}

	private boolean expandParallel(ParallelNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						

		if (behavior.getSteps().size() == 0) {
			throw new ABTRuntimeError("Behavior has no child steps: " + behavior.getGoalName());
		}

		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
		}
  		
		return true;
	}

	private boolean expandSequential(SequentialNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						
		
		if (behavior.getSteps().size() == 0) {
			throw new ABTRuntimeError("Behavior has no child steps: " + behavior.getGoalName());
		}
 
		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
			break;   
		}
 		
		return true;
	}
 
	private void expandBehaviorStep(BehaviorNode behavior, ABTNode step) {		
		if (!step.getPrioritySpecified()) {
			step.setPriority(behavior.getPriority());
		}
		  
		if (step instanceof SpawnGoalNode) { 

			// bind the parameters now, since the spawned goal will not have a parent behavior 
			((SpawnGoalNode)step).setParameters(bindVariables(behavior, ((SpawnGoalNode)step).getParameters()));
 			
			rootNodes.add(step);
			behavior.childCompleted(step);
		}
		else {
			behavior.addChild(step);
			step.setParent(behavior); 
			
		}
	}

	private boolean expandGoal(GoalNode goal) {
//		System.out.println("Expanding goal: " + goal);
		 
		// bind goal parameters (note: spawngoal variables will already be bound) 
		Object[] goalParameters = bindVariables(goal, goal.getParameters());

		// find match behaviors in the library 
		ArrayList<BehaviorPrototype> matching = new ArrayList<BehaviorPrototype>();
		for (BehaviorPrototype prototype : behaviorLibrary) {
			if (prototype.matchingSignature(goal.getGoalName(), goalParameters)) {

				// dont retry behaviors for the same goal 
				if (!goal.hasAttempted(prototype)) {
					matching.add(prototype);
				}
			}  
		}
 
		// sort by specificity  
		Collections.sort(matching, new Comparator<BehaviorPrototype>() {
			public int compare(BehaviorPrototype b1, BehaviorPrototype b2) {
				if (b1.getSpecificity() != b2.getSpecificity()) {
					return b2.getSpecificity() - b1.getSpecificity();
				} 
				else {
					return b1.hashCode() - b2.hashCode();
				}
			}			
		});
  
		// expand the first matching behavior 
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
	
	private boolean checkConditions(HashMap<String, Object> variables, ArrayList<ConditionPrototype> conditions, int index) {
  
		// all conditions are satisfied 
		if (index == conditions.size()) {
			return true;
		} 

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
			// TODO: support mental conditions 
			String method = condition.getMethodName();
			System.err.println(method);
			
//			bindVariables(node, parameters)
			
			return true;  
		}
	}

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
