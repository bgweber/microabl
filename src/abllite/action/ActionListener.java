package abllite.action;

import abllite.abt.ActionNode;

public interface ActionListener {
 
	public void execute(ActionNode action);

	public void abort(ActionNode action);
	
	// TODO: on update? 
}
 