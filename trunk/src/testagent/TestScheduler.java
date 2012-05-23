package testagent;

import microabl.ActionListener;
import microabl.abt.ActionNode;
import microabl.abt.ABTNode.NodeStatus;

public class TestScheduler implements ActionListener {

	public void execute(final ActionNode action) {
		System.out.println("Executing action: " + action.getActionName());
		for (Object p : action.getExecutionParameters()) {
			System.out.println("  param: " + p);
		}
		
		 
		if (action.getActionName().equals("WaitMS")) {
			final int timeout = (Integer)action.getExecutionParameters()[0];
			
			new Thread() {
				public void run() {
					try {
						Thread.sleep(timeout);
					}
					catch (Exception e) {}
					 
					action.setStatus(NodeStatus.Success);
				}
			}.start();
		}
		else {
			action.setStatus(NodeStatus.Success);
		}
	}
	 
	public void abort(ActionNode action) {
		System.err.println("Aborting action: " + action.getActionName());
	}
	
	public void onUpdate(ActionNode action) {
		
	}
}
