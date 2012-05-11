package abllite.abt;

public class SucceedStepNode extends ABTNode {
 
	public SucceedStepNode() {
		setStatus(NodeStatus.Success);
	}
 
	public String toString() {
		return "SucceedStepNode";
	}
}
