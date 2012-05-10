package testagent;

import abllite.abt.ActionNode;
import abllite.abt.ABTNode.NodeStatus;
import abllite.action.ActionListener;

public class TestScheduler implements ActionListener {

	public void execute(final ActionNode action) {
		System.out.println("Executing action: " + action.getActionName());
		
		
		if (action.getActionName().equals("WaitMS")) {
			final int timeout = (Integer)action.getParameters()[0];
			
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
	}
}
