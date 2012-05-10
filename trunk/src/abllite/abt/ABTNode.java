package abllite.abt;

import java.util.ArrayList;

public class ABTNode {
	
	public enum NodeStatus { Open, Executing, Success, Failure }

	protected NodeStatus nodeStatus = NodeStatus.Open;
	
	private ABTNode parent; 
	
	private int priority;
	
	private ArrayList<ABTNode> children = new ArrayList<ABTNode>(); 
	
	
	public ArrayList<ABTNode> getChildren() {
		return children;
	}
	
	public String toString() {
		return "ABTNode" + getClass();
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public boolean isOpen() {
		return nodeStatus == NodeStatus.Open;
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
	
	public void childCompleted(ABTNode node) {
		System.err.println(getClass());
		Thread.dumpStack();		
	}
}
 