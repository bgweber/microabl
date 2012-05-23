package microabl;
 
import microabl.abt.ActionNode;
/**
 * Interface for handling actions selected for execution by an ABL agent. 
 */
public interface ActionListener {
 
	/**
	 * Informs the listener that an action has been selected for execution. 
	 */
	public void execute(ActionNode action);

	/**
	 * Informs the listener that the action is executing during a decision cycle. 
	 */
	public void onUpdate(ActionNode action);

	/**
	 * Informs the listener that an executing action should be aborted. 
	 */
	public void abort(ActionNode action);
}
 