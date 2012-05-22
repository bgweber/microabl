package abllite.abt;
/**
 * Represents a success step. 
 */
public class SucceedStepNode extends ABTNode {
 
	/**
	 * Immediately set status to success. 
	 */
	public SucceedStepNode() {
		setStatus(NodeStatus.Success);
	}
 
	public String toString() {
		return "SucceedStepNode";
	}
}
