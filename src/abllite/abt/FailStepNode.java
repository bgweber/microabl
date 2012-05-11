package abllite.abt;

public class FailStepNode extends ABTNode {

	public FailStepNode() {
		setStatus(NodeStatus.Failure);
	}
	 
	public String toString() {
		return "FailStepNode";
	}
}
 