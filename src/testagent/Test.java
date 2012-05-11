package testagent;

import java.util.ArrayList;

import abllite.Agent;
import abllite.abt.ABT;
import abllite.prototype.BehaviorPrototype;
import abllite.prototype.ConditionPrototype;
import abllite.prototype.StepPrototype;
import abllite.prototype.Variable;
import abllite.prototype.ConditionPrototype.Comparison;

public class Test {   
    
	public static void main(String[] args) {
   
		TestScheduler scheduler = new TestScheduler();
		ArrayList<BehaviorPrototype> behaviorLibrary = new ArrayList<BehaviorPrototype>();
   
		ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>(); 
//		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { "Chaser Agent" }).setPriority(4));
//		steps.add(StepPrototype.createSubgoal("manageFiring").setPriority(2));  
		steps.add(StepPrototype.createSubgoal("fire").setPriority(2));  
//		steps.add(StepPrototype.createSubgoal("manageMovement").setPriority(1)); 
//		steps.add(StepPrototype.createSpawngoal("detectCollisions").setPriority(3)); 
		behaviorLibrary.add(BehaviorPrototype.createParallel(ABT.INITIAL_GOAL).setSteps(steps));
   
//		steps = new ArrayList<StepPrototype>();
//		steps.add(StepPrototype.createAction("WaitMS").setParameters(new Object[] { 150 })); 
//		steps.add(StepPrototype.createSubgoal("fire").setModifier(StepModifier.Persistent));
//		behaviorLibrary.add(BehaviorPrototype.createSequential("manageFiring").setSteps(steps)); 
    
		ArrayList<ConditionPrototype> preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX").addBinding("y", "playerY"));
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).
				addBinding("name", "chaserName").
				setWMEVariable("chaser").
				addTest("name", Comparison.Equals, "Chaser").
				addTest("x", Comparison.lt, 100). 
				addTest("x", Comparison.lte, new Variable("playerX")));  
		preconditions.add(ConditionPrototype.createNegation(BulletWME.class));
		     
		 
		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction("fire").setParameters(new Object[] { new Variable("playerX"), new Variable("playerY") }));  
		steps.add(StepPrototype.createSpawngoal("print").setParameters(new Object[] { "Hello spawngoal", new Variable("playerX") }).setPriority(6));
//		steps.add(StepPrototype.createSubgoal("fire").setParameters(new Object[] { new Variable("playerX"), new Variable("playerY") }));  
//		steps.add(StepPrototype.createAction("WaitMS").setParameters(new Object[] { 150 })); 
//		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { "Chaser Agent" }).setPriority(4));
		
		behaviorLibrary.add(BehaviorPrototype.createSequential("fire").setSteps(steps).setPreconditions(preconditions));
  
		
//		// behavior with parameters 
//		steps = new ArrayList<StepPrototype>();  
//		steps.add(StepPrototype.createAction("fire").setParameters(new Object[] { new Variable("fireX"), new Variable("fireY") }));  
//
//		behaviorLibrary.add(BehaviorPrototype.createSequential("fire").
//				addParameter(Integer.class, "fireX").
//				addParameter(Integer.class, "fireY"). 
//				setSteps(steps).
//				setPreconditions(preconditions));
// 
//		// behavior that logs an input message 
//		steps = new ArrayList<StepPrototype>();  
//		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { new Variable("message"), new Variable("x") }));  
//		steps.add(StepPrototype.createSucceedStep());
//		steps.add(StepPrototype.createFailStep());
//		 
//		behaviorLibrary.add(BehaviorPrototype.createSequential("print").
//				setSpecificity(2).
//				addParameter(String.class, "message").
//				addParameter(Integer.class, "x").
//				setSteps(steps));
//
//		steps = new ArrayList<StepPrototype>();  
//		steps.add(StepPrototype.createSucceedStep());
//		
//		behaviorLibrary.add(BehaviorPrototype.createSequential("print").
//				setSpecificity(1).
//				addParameter(String.class, "message").
//				addParameter(Integer.class, "x").
//				setSteps(steps));

		
//		steps = new ArrayList<StepPrototype>();
//		steps.add(StepPrototype.createSubgoal("move").setModifier(StepModifier.Persistent));
//		behaviorLibrary.add(BehaviorPrototype.createSequential("manageMovement").setSteps(steps));
// 
//		steps = new ArrayList<StepPrototype>();
//		steps.add(StepPrototype.createAction("moveUp").setParameters(new Object[] { "Chaser Agent" }));
//		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSteps(steps).setSpecificity(3));
 
//		steps = new ArrayList<StepPrototype>();
//		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { "Started new goal" }));
//		steps.add(StepPrototype.createAction("WaitMS").setParameters(new Object[] { 250 })); 
//		steps.add(StepPrototype.createAction("log").setParameters(new Object[] { "finished new goal" }));
//		behaviorLibrary.add(BehaviorPrototype.createSequential("detectCollisions").setSteps(steps));
		
		
		Agent agent = new Agent(behaviorLibrary, scheduler); 
		
		PlayerWME playerWME = new PlayerWME();
		ChaserWME chaserWME = new ChaserWME();
		agent.getWorkingMemory().addWME(playerWME); 
		agent.getWorkingMemory().addWME(chaserWME); 
		agent.getWorkingMemory().dump();
		
 		for (int i=0; i<=6; i++) { 
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
			  
			chaserWME.setX(chaserWME.getX() + 10);
			chaserWME.setY(chaserWME.getY() + 5);
		}
				
		agent.getWorkingMemory().dump();
	}
}
