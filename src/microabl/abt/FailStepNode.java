package microabl.abt;
/**
 * Represents a failure step. 
 */
public class FailStepNode extends ABTNode {

	/**
	 * Immediately set status to failure. 
	 */
	public FailStepNode() {
		setStatus(NodeStatus.Failure);
	}
	 
	public String toString() {
		return "FailStepNode";
	}
}
 