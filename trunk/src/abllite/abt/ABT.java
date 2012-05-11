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

	public void performDecisionCycle() {
		
		// tree finished
		if (rootNodes.size() == 0) {
			return; 
		}
		 
		// find completed nodes
		ArrayList<ABTNode> completed = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findCompletedNodes(root, completed);
		}
		
//		System.out.println("completed: " + completed);
		for (ABTNode node : completed) {
			ABTNode parent = node.getParent();
			
			if (parent == null) {
				rootNodes.remove(node);
 				
				if (rootNodes.size() == 0) {
					return; 
				}
			}  
			else {  
				parent.getChildren().remove(node);				
				parent.childCompleted(node);
			}
		}
 	 
		// find open nodes
		ArrayList<ABTNode> available = new ArrayList<ABTNode>();		
		for (ABTNode root : rootNodes) {
			findOpenNodes(root, available);
		} 
		
		// sort available nodes based on priority   
		Collections.sort(available, new Comparator<ABTNode>() {
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
		for (ABTNode node : available) {
			if (expand(node)) {
				break; 
			}
		}
		
		// TODO: return result? 
	}
 	
	public void findCompletedNodes(ABTNode node, ArrayList<ABTNode> completed) {

		if (node.getChildren().size() > 0) {
			for (ABTNode child : node.getChildren()) {
				findCompletedNodes(child, completed);
			}			
		}
		else if (node.isCompleted()) {
			completed.add(node);
		}
		
	}  
	  
	public void findOpenNodes(ABTNode node, ArrayList<ABTNode> available) {
		if (node.isOpen()) {
			available.add(node);
		}
		
		for (ABTNode child : node.getChildren()) {
			findOpenNodes(child, available);
		}
	}
	
	public boolean expand(ABTNode node) {
		System.err.println("Trying to expand: " + node);
		
		
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

		return false; 
	}

	public Object getVariable(ABTNode node, String name) {

 		// get the parent behavior 
		while (node != null && !(node instanceof BehaviorNode)) {
			node = node.getParent();
		}

		if (node == null) {
			throw new ABTRuntimeError("Step has no parent behavior");  
		}
		
		return (node != null) ? ((BehaviorNode)node).getVariable(name) : null;
	}
 
	public Object[] bindVariables(ABTNode node, Object[] parameters) {
		Object[] executionParameters = new Object[parameters.length];
 		 
		for (int index=0; index<parameters.length; index++) {
			executionParameters[index] = (parameters[index] instanceof Variable) ? getVariable(node, (((Variable)parameters[index]).getName())) : parameters[index];
		}
 
		return executionParameters;
	}
 
	public boolean expandModifier(ModifierNode modifier) {
		modifier.setStatus(NodeStatus.Executing); 						
		
		ABTNode child = modifier.createABTNode();
		modifier.addChild(child);
		child.setParent(modifier); 
		child.setPriority(modifier.getPriority());
		
		return true;
	}
	 
	public boolean expandAction(ActionNode action) {
 
		action.setParameters(bindVariables(action, action.getParameters())); 
		action.setStatus(NodeStatus.Executing); 						
		actionListener.execute(action);
		return true;
	}

	public boolean expandParallel(ParallelNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						
  		
		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
		}
  		
		return true;
	}

	public boolean expandSequential(SequentialNode behavior) {
		behavior.setStatus(NodeStatus.Executing); 						
 
		for (ABTNode step : behavior.getSteps()) {
			expandBehaviorStep(behavior, step);
			break;   
		}
 		
		return true;
	}
 
	public void expandBehaviorStep(BehaviorNode behavior, ABTNode step) {		
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


	public boolean expandGoal(GoalNode goal) {
		System.out.println("Expanding goal: " + goal);
		 
		// bind goal parameters
		Object[] goalParameters = bindVariables(goal, goal.getParameters());

		// find match behaviors in the library 
		ArrayList<BehaviorPrototype> matching = new ArrayList<BehaviorPrototype>();
		for (BehaviorPrototype prototype : behaviorLibrary) {
			if (prototype.matchingSignature(goal.getGoalName(), goalParameters)) {
				matching.add(prototype);
			}  
		}

		// TODO: sort by specificity 
 
		// expand the first matching behavior 
		for (BehaviorPrototype prototype : matching) {

			// match goal parameters to behavior properties  
			HashMap<String, Object> variables = prototype.bindVariables(goalParameters); 
			
			// check preconditions
			System.out.println("Preconditions: " + prototype.getPreconditions());
			if (!checkConditions(variables, prototype.getPreconditions(), 0)) {
				continue; 
			}
   
			// expand the behavior 
			BehaviorNode behavior = prototype.isSequential() ? new SequentialNode(prototype, variables) : new ParallelNode(prototype, variables);
			behavior.setParent(goal);
			goal.addChild(behavior); 
			behavior.setPriority(goal.getPriority());
 
			behavior.setVariables(variables);			
			goal.setStatus(NodeStatus.Executing); 						
			return true;
		}
		 
		// no matching behaviors found 
		return false;
	}
	
	private boolean checkConditions(HashMap<String, Object> properties, ArrayList<ConditionPrototype> conditions, int index) {
 
		// all conditions are satisfied 
		if (index == conditions.size()) {
			return true;
		}

		ConditionPrototype condition = conditions.get(index);
  
		// check for the existence of a WME 
		if (condition.isWMECheck()) {
			  
			HashSet<WME> wmes = workingMemory.getWMEs(condition.getWMEClass());
			for (WME wme : wmes) {

				// TODO: check is wme matches conditions
				 
				// bind properties
				HashMap<String, String> bindings = condition.getBindings();
				for (String attribute : bindings.keySet()) {
					properties.put(bindings.get(attribute), wme.getAttribute(attribute));
				}

				// bind wme instance to a property
				if (condition.getWMEProperty() != null) {
					properties.put(condition.getWMEProperty(), wme);
				}
 				 
				// recurse!!! 
				if (checkConditions(properties, conditions, index + 1)) {
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
  
				// TODO: check is wme matches conditions

				// if WME conditions match 
				return false; 
			}

			// recurse!!! 
			if (checkConditions(properties, conditions, index + 1)) {
				return true;
			}					
		}

		Thread.dumpStack();
		return true;  
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
	
	public void print(ABTNode node, int depth) {
		for (int i=0; i<depth; i++) System.out.print("  ");
		
		System.out.println("- " + node.toString());
		
		for (ABTNode child : node.getChildren()) {
			print(child, depth + 1);
		}
	}
}
