package microabl.abt;

import java.util.ArrayList;
/**
 * Base class for nodes in the ABT. 
 * 
 * Node statuses: 
 *  Open: node is available for expansion 
 *  Executing: node is currently executing 
 *  Success: node completed and succeeded 
 *  Failure: node completed and failed 
 */
public abstract class ABTNode {

	/** possible status for a node */ 
	public enum NodeStatus { Open, Executing, Success, Failure }
 
	/** status of the node */
	protected NodeStatus nodeStatus = NodeStatus.Open;
	
	/** the node parent (null if a root-level goal) */ 
	private ABTNode parent; 

	/** priority of the node */
	private int priority;

	/** specifies if priority has been assigned for this node. Defaults to false, which inherits parent priority */ 
	private boolean prioritySpecified = false;
	
	/** child nodes (for goals this is a single behavior, for behaviors it is set of steps) */ 
	private ArrayList<ABTNode> children = new ArrayList<ABTNode>(); 
	
	/** Informs the node that a child has completed. Base implementation does nothing. */
	public void childCompleted(ABTNode node) {		
	}

	public int getNumChildren() {
		return children.size();
	}
  
	public Iterable<ABTNode> getChildren() {
		return children;
	}
	 
	public void removeChild(ABTNode child) {
		children.remove(child);
	}

	public void clearChildren() {
		children.clear();
	}
 
	public int getPriority() {
		return priority;
	} 
	
	public void setPriority(int priority) {
		this.priority = priority;
		prioritySpecified = true;
	}
	 
	public boolean getPrioritySpecified() {
		return prioritySpecified;
	}
	
	public boolean isOpen() {
		return nodeStatus == NodeStatus.Open;
	}
	 
	public boolean isExecuting() {
		return nodeStatus == NodeStatus.Executing;
	}

	public boolean isSuccess() {
		return nodeStatus == NodeStatus.Success;
	}

	public boolean isFailure() {
		return nodeStatus == NodeStatus.Failure;
	}

	public boolean isCompleted() {
		return nodeStatus == NodeStatus.Success || nodeStatus == NodeStatus.Failure;
	}

	public void setStatus(NodeStatus status) {
		this.nodeStatus = status;
	}
	
	public void setParent(ABTNode parent) {
		this.parent = parent; 
	}

	public void addChild(ABTNode child) {
		children.add(child);
	}
	
	public ABTNode getParent() {
		return parent;
	}
	
	public String toString() {
		return "ABTNode" + getClass();
	}
}
 