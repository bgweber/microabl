package abllite.abt;

import abllite.prototype.StepPrototype;
import abllite.prototype.StepPrototype.StepModifier;

public class ModifierNode extends ABTNode {

	private StepPrototype stepPrototype; 
	
	private StepModifier modifier; 
 
	public ModifierNode(StepPrototype stepPrototpye, StepModifier modifier) {
		this.stepPrototype = stepPrototpye; 
		this.modifier = modifier;
	} 

 	public ABTNode createABTNode() {
		return stepPrototype.createABTNode(true);
	}
 
	public String toString() {  
		return "Modifier: " + modifier + " (" + nodeStatus + ") " + getPriority(); 
	}
	
	
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
}
