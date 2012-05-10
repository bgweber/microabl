package abllite;

import java.util.ArrayList;

import abllite.abt.ABT;
import abllite.action.ActionListener;
import abllite.prototype.BehaviorPrototype;
import abllite.wm.WorkingMemory;

public class Agent {
	
	private WorkingMemory workingMemory = new WorkingMemory();

	private ABT abt;	

	public Agent(ArrayList<BehaviorPrototype> behaviorLibrary, ActionListener actionListener) {
		abt = new ABT(behaviorLibrary, workingMemory, actionListener);
	}
	  
	public void tick() {
		abt.performDecisionCycle();
	}
	
	public void printABT() {
		abt.printABT();
	}
 
	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	}
}
 