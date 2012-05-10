package abllite.abt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import abllite.abt.ABTNode.NodeStatus;
import abllite.action.ActionListener;
import abllite.prototype.BehaviorPrototype;
import abllite.wm.WorkingMemory;

public class ABT {
	
	private ArrayList<BehaviorPrototype> behaviorLibrary; 
	
	private ActionListener actionListener; 
 
	private WorkingMemory workingMemory;

	private ABTNode rootNode;  
	
	public static String INITIAL_GOAL = "init_tree"; 

	public ABT(ArrayList<BehaviorPrototype> behaviorLibrary, WorkingMemory workingMemory, ActionListener actionListener) {
		this.behaviorLibrary = behaviorLibrary;
		this.workingMemory = workingMemory;
		this.actionListener = actionListener;		
		 
		rootNode = new GoalNode(INITIAL_GOAL, new Object[0]);
	}

	public void performDecisionCycle() {
		
		// tree finished
		if (rootNode == null) {
			return; 
		}
		
		// find completed nodes
		ArrayList<ABTNode> completed = new ArrayList<ABTNode>();		
		findCompletedNodes(rootNode, completed);
		
		System.out.println("completed: " + completed);
		for (ABTNode node : completed) {
		
			ABTNode parent = node.getParent();
			
			if (parent == null) {
				rootNode = null;
				return; 
			}  
			else {  
				parent.getChildren().remove(node);				
				parent.childCompleted(node);
			}
		}
 	 
		// find open nodes
		ArrayList<ABTNode> available = new ArrayList<ABTNode>();		
		findOpenNodes(rootNode, available);
		
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
		 
		System.out.println("open: " + available);
		for (ABTNode node : available) {
			if (expand(node)) {
				break; 
			}
		}
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

	public boolean expandModifier(ModifierNode modifier) {
		modifier.setStatus(NodeStatus.Executing); 						
		
		ABTNode child = modifier.createABTNode();
		modifier.addChild(child);
		child.setParent(modifier); 
		child.setPriority(modifier.getPriority());
		
		return true;
	}

	public boolean expandAction(ActionNode action) {
		action.setStatus(NodeStatus.Executing); 						
		actionListener.execute(action);
		  
		return true;
	}

	public boolean expandParallel(ParallelNode behavior) {
 		
		for (ABTNode step : behavior.getSteps()) {
			behavior.addChild(step);
			step.setParent(behavior); 
		}
 		
		behavior.setStatus(NodeStatus.Executing); 						
		return true;
	}

	public boolean expandSequential(SequentialNode behavior) {
 		
		for (ABTNode step : behavior.getSteps()) {
			behavior.addChild(step);
			step.setParent(behavior); 
			break;  
		}
 		
		behavior.setStatus(NodeStatus.Executing); 						
		return true;
	}

	public boolean expandGoal(GoalNode goal) {
		System.out.println("Expanding goal: " + goal);
		
		// find match behaviors in the library 
		ArrayList<BehaviorPrototype> matching = new ArrayList<BehaviorPrototype>();
		for (BehaviorPrototype prototype : behaviorLibrary) {
			if (prototype.matchingSignature(goal.getGoalName(), goal.getParameters())) {
				matching.add(prototype);
			} 
		}
		 

		for (BehaviorPrototype prototype : matching) {

			// TODO: precondition check 
			 
			// expand the behavior 
			BehaviorNode behavior = new SequentialNode(prototype);
			if (prototype.isSequential()) {
				behavior = new SequentialNode(prototype);
				behavior.setParent(goal);
				goal.addChild(behavior); 
				behavior.setPriority(goal.getPriority());
			}
			else if (prototype.isParallel()) {
				behavior = new ParallelNode(prototype);
				behavior.setParent(goal);
				goal.addChild(behavior); 
				behavior.setPriority(goal.getPriority());
			}			  
 			
			goal.setStatus(NodeStatus.Executing); 						
		}
		
		return true;
	}
	
	
	public void printABT() {
		if (rootNode != null) {
			print(rootNode, 0);
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
