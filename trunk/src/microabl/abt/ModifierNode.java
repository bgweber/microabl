package microabl.abt;

import microabl.prototype.StepPrototype;
import microabl.prototype.StepPrototype.StepModifier;
/**
 * Represents a modifier node, which wraps a step node. 
 */
public class ModifierNode extends ABTNode {

	/** the step this prototype wraps */ 
	private StepPrototype stepPrototype; 
	
	/** the type of modifier */ 
	private StepModifier modifier; 
	
	/** 
	 * Creates a modifier node for the given step prototype and modifier type. 
	 */
	public ModifierNode(StepPrototype stepPrototpye, StepModifier modifier) {
		this.stepPrototype = stepPrototpye; 
		this.modifier = modifier;
	} 

	/**
	 * Creates a ABT node for the step this modifier wraps. 
	 */
 	public ABTNode createABTNode() {
		return stepPrototype.createABTNode();
	}
 	 
 	/**
 	 * Modifies the success/failure of the child based on modifier type. 
 	 */
	public void childCompleted(ABTNode node) {
		
		if (modifier == StepModifier.Persistent) {
			setStatus(NodeStatus.Open);
		} 
		else if (modifier == StepModifier.IgnoreFailure) {
			setStatus(NodeStatus.Success);
		}
		else if (modifier == StepModifier.PersistentWhenFails) {
			setStatus(node.isSuccess() ? NodeStatus.Success : NodeStatus.Open);
		}
		else if (modifier == StepModifier.PersistentWhenSucceeds) { 
			setStatus(node.isSuccess() ? NodeStatus.Open : NodeStatus.Failure);
		} 	
	}

	public String toString() {  
		return "Modifier: " + modifier + " (" + nodeStatus + ") " + getPriority(); 
	}
}
