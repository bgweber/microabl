package testagent;

import java.util.ArrayList;

import abllite.Agent;
import abllite.abt.ABT;
import abllite.prototype.BehaviorPrototype;
import abllite.prototype.StepPrototype;
import abllite.prototype.StepPrototype.StepModifier;

public class Test {

	public static void main(String[] args) {
  
		TestScheduler scheduler = new TestScheduler();
		ArrayList<BehaviorPrototype> behaviorLibrary = new ArrayList<BehaviorPrototype>();
  
		ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>(); 
		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { "Chaser Agent" }).setPriority(3));
		steps.add(StepPrototype.createSubgoal("manageFiring").setPriority(2));
		steps.add(StepPrototype.createSubgoal("manageMovement").setPriority(1)); 
		behaviorLibrary.add(BehaviorPrototype.createParallel(ABT.INITIAL_GOAL).setSteps(steps));
 
		steps = new ArrayList<StepPrototype>();
		steps.add(StepPrototype.createAction("WaitMS").setParameters(new Object[] { 150 })); 
		steps.add(StepPrototype.createSubgoal("fire").setModifier(StepModifier.Persistent));
		behaviorLibrary.add(BehaviorPrototype.createSequential("manageFiring").setSteps(steps));
  
		steps = new ArrayList<StepPrototype>(); 
		steps.add(StepPrototype.createAction("fire"));  
		steps.add(StepPrototype.createAction("WaitMS").setParameters(new Object[] { 150 })); 
		behaviorLibrary.add(BehaviorPrototype.createSequential("fire").setSteps(steps));
		
		steps = new ArrayList<StepPrototype>();
		steps.add(StepPrototype.createSubgoal("move").setModifier(StepModifier.Persistent));
		behaviorLibrary.add(BehaviorPrototype.createSequential("manageMovement").setSteps(steps));
 
		steps = new ArrayList<StepPrototype>();
		steps.add(StepPrototype.createAction("moveUp").setParameters(new Object[] { "Chaser Agent" }));
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSteps(steps).setSpecificity(3));

 
//		sequential behavior move() {
//			precondition {
//				(PlayerWME locationY::playerY)
//				(ChaserWME locationY::chaserY) 
//				(chaserY > (playerY + ChaserSpeed))
//			}
//			specificity 3;
//	 
//			act moveUp();
//		}
		
		
		Agent agent = new Agent(behaviorLibrary, scheduler); 
		
		PlayerWME playerWME = new PlayerWME();
		ChaserWME chaserWME = new ChaserWME();
		agent.getWorkingMemory().addWME(playerWME); 
		agent.getWorkingMemory().addWME(chaserWME); 
		agent.getWorkingMemory().dump();
		
 		for (int i=0; i<25; i++) { 
			System.out.println("\nABT Cycle " + i);
			System.out.println("-----------------");
			agent.printABT();
	 
			System.out.println();
			agent.tick();
 
			try {
				Thread.sleep(100);
			}
			catch (Exception e) {}
			
			playerWME.setX(playerWME.getX() + 20);
			playerWME.setY(playerWME.getY() + 10);
		}
				
		agent.getWorkingMemory().dump();
	}
}
