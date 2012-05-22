package abllite.action;
 
import abllite.abt.ActionNode;
/**
 * Interface for handling actions selected for execution by an ABL agent. 
 */
public interface ActionListener {
 
	/**
	 * Informs the listener that an action has been selected for execution. 
	 */
	public void execute(ActionNode action);

	/**
	 * Informs the listener that an executing action should be aborted. 
	 */
	public void abort(ActionNode action);
}
 