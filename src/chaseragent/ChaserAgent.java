package chaseragent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import microabl.ActionListener;
import microabl.Agent;
import microabl.abt.ActionNode;
import microabl.abt.ABTNode.NodeStatus;
import microabl.prototype.BehaviorPrototype;
import microabl.prototype.ConditionPrototype;
import microabl.prototype.StepPrototype;
import microabl.prototype.Variable;
import microabl.prototype.StepPrototype.StepModifier;

public class ChaserAgent implements ActionListener {
 
	// actions 
	public static String LogAction = "Log";
	public static String WaitAction = "Wait";
	public static String FireAction = "Fire";
	public static String MoveLeftAction = "MoveLeft";
	public static String MoveRightAction = "MoveRight";
	public static String MoveUpAction = "MoveUp";
	public static String MoveDownAction = "MoveDown";
	public static String StopAction = "Stop";
	 	
	/** a reference to the game */ 
	private Game game; 
	
	/** the ABL agent */
	private Agent agent;
	
	/** WME for tracking the chaser */	
	private ChaserWME chaser; 
	
	/** WME for tracking the player */	
	private PlayerWME player; 
	
	public ChaserAgent(Game game) {
		this.game = game; 
		
		agent = new Agent(getBehaviorLibrary(), this);

		// set up working memory 
		chaser = new ChaserWME();
		chaser.setX(game.getChaserLocation().x);
		chaser.setY(game.getChaserLocation().y);
		agent.getWorkingMemory().addWME(chaser);
 		
		player = new PlayerWME();
		player.setX(game.getPlayerLocation().x);
		player.setY(game.getPlayerLocation().y);
		agent.getWorkingMemory().addWME(player);		
	}
	
	public Agent getAgent() {
		return agent; 
	}
	
	public ArrayList<BehaviorPrototype> getBehaviorLibrary() {
		ArrayList<BehaviorPrototype> behaviorLibrary = new ArrayList<BehaviorPrototype>();

		/*
	    initial_tree {
	    	with (priority 3) mental_act {
				System.out.println("Starting Chaser Agent");
	    	}
	     	
	    	with (priority 2) subgoal manageFiring();
	    	with (priority 1) subgoal manageMovement();
	    }*/

		ArrayList<StepPrototype> steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(LogAction).setParameters(new Object[] { "Starting Chaser Agent" }).setPriority(3));
		steps.add(StepPrototype.createSubgoal("manageFiring").setPriority(2));  
		steps.add(StepPrototype.createSubgoal("manageMovement").setPriority(1)); 
		behaviorLibrary.add(BehaviorPrototype.createParallel(Agent.INITIAL_GOAL).setSteps(steps));		
 		
		/*
		sequential behavior manageFiring() {
			subgoal Wait(2000);					// this suspends the execution of the behavior
			with (persistent) subgoal fire();
		}*/
		 
		steps = new ArrayList<StepPrototype>();
		steps.add(StepPrototype.createAction(WaitAction).setParameters(new Object[] { 2000 })); 
		steps.add(StepPrototype.createSubgoal("fire").setModifier(StepModifier.Persistent));
		behaviorLibrary.add(BehaviorPrototype.createSequential("manageFiring").setSteps(steps)); 
		
		/*
		sequential behavior fire() {
			precondition {
				(PlayerWME locationX::playerX locationY::playerY)	// locationX invokes PlayerMWE.getLocationX() 
				(ChaserWME locationX::chaserX locationY::chaserY)   // :: is used to bind properties to behavior scoped variables
			}

			act fire(chaserX, chaserY, playerX, playerY);	
			subgoal Wait(500); 
		}*/
 
		ArrayList<ConditionPrototype> preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("x", "chaserX").addBinding("y", "chaserY"));
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX").addBinding("y", "playerY"));

		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(FireAction).setParameters(new Object[] { 
				new Variable("chaserX"), new Variable("chaserY"), new Variable("playerX"), new Variable("playerY") }));  
		steps.add(StepPrototype.createAction(WaitAction).setParameters(new Object[] { 500 })); 
		behaviorLibrary.add(BehaviorPrototype.createSequential("fire").setSteps(steps).setPreconditions(preconditions)); 		
 		
		/*
		sequential behavior manageMovement() { 
			with (persistent) subgoal move();
		}*/ 
 
		steps = new ArrayList<StepPrototype>();
		steps.add(StepPrototype.createSubgoal("move").setModifier(StepModifier.Persistent));
		behaviorLibrary.add(BehaviorPrototype.createSequential("manageMovement").setSteps(steps));

		/*
		sequential behavior move() {
			precondition {
				(PlayerWME locationY::playerY)
				(ChaserWME locationY::chaserY) 
				(chaserY > (playerY + ChaserSpeed))
			}
			specificity 3;
	 
			act moveUp();
		}*/ 
 
		preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("y", "chaserY")); 
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("y", "playerY")); 
		preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("greaterThanSum").setMethodParameters(
				new Object[] { new Variable("chaserY"), new Variable("playerY"), Game.ChaserSpeed} ));
 
		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(MoveUpAction));  
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(3).setSteps(steps).setPreconditions(preconditions)); 		
 
		/*
		sequential behavior move() {
			precondition {
				(PlayerWME locationY::playerY)
				(ChaserWME locationY::chaserY) 
				(chaserY < (playerY - ChaserSpeed))
			}
			specificity 3;

			act moveDown();
		}*/

		preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("y", "chaserY")); 
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("y", "playerY"));
		preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("lessThanDiff").setMethodParameters(
				new Object[] { new Variable("chaserY"), new Variable("playerY"), Game.ChaserSpeed} )); 

		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(MoveDownAction));  
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(3).setSteps(steps).setPreconditions(preconditions)); 		

		/*
		sequential behavior move() {
			precondition {
				(PlayerWME locationX::playerX)
				(ChaserWME locationX::chaserX) 
				(chaserX > (playerX + ChaserSpeed))
			}
			specificity 2;

			act moveLeft();
		}*/ 

		preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("x", "chaserX")); 
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX")); 
		preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("greaterThanSum").setMethodParameters(
				new Object[] { new Variable("chaserX"), new Variable("playerX"), Game.ChaserSpeed} )); 

		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(MoveLeftAction));  
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(2).setSteps(steps).setPreconditions(preconditions)); 		

		/*
		sequential behavior move() {
			precondition { 
				(PlayerWME locationX::playerX)
				(ChaserWME locationX::chaserX) 
				(chaserX < (playerX - ChaserSpeed))
			}
			specificity 2;

			act moveRight();
		}*/		
 
		preconditions = new ArrayList<ConditionPrototype>();
		preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("x", "chaserX")); 
		preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX"));
		preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("lessThanDiff").setMethodParameters(
				new Object[] { new Variable("chaserX"), new Variable("playerX"), Game.ChaserSpeed} )); 

		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(MoveRightAction));  
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(2).setSteps(steps).setPreconditions(preconditions)); 		

		/*
		sequential behavior move() {
			specificity 1;
			act stop();
			subgoal Wait(1000); 
		}
		*/ 
		
		steps = new ArrayList<StepPrototype>();  
		steps.add(StepPrototype.createAction(StopAction));  
		steps.add(StepPrototype.createAction(WaitAction).setParameters(new Object[] { 1000 })); 
		behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(1).setSteps(steps)); 		
				
		return behaviorLibrary; 
	}
	
	public void update() {
 		
		// update WMEs
		chaser.setX(game.getChaserLocation().x);
		chaser.setY(game.getChaserLocation().y);
		
		player.setX(game.getPlayerLocation().x);
		player.setY(game.getPlayerLocation().y);

		// perform the decision cycle 
		while (agent.update());

//		agent.getWorkingMemory().dump();
//		agent.printABT();
	} 
	
	private HashMap<ActionNode, Long> waitActions = new HashMap<ActionNode, Long>(); 
 
	public void execute(ActionNode action) {

		// log action
		if (action.getActionName().equals(LogAction)) {
			System.out.println(action.getExecutionParameters()[0]);
			action.setStatus(NodeStatus.Success);
		}
		// fire 
		else if (action.getActionName().equals(FireAction)) {
			game.fireChaserBullet( 
					new Point((Integer)action.getExecutionParameters()[0], (Integer)action.getExecutionParameters()[1]), 
					new Point((Integer)action.getExecutionParameters()[2], (Integer)action.getExecutionParameters()[3]));
						
			action.setStatus(NodeStatus.Success);
		}
		// move left
		else if (action.getActionName().equals(MoveLeftAction)) {			
			game.moveChaserLeft();
			action.setStatus(NodeStatus.Success);
		}
		// move right
		else if (action.getActionName().equals(MoveRightAction)) {			
			game.moveChaserRight();
			action.setStatus(NodeStatus.Success);
		}
		// move up
		else if (action.getActionName().equals(MoveUpAction)) {			
			game.moveChaserUp();
			action.setStatus(NodeStatus.Success);
		} 
 		// move down  
		else if (action.getActionName().equals(MoveDownAction)) {			
			game.moveChaserDown();
			action.setStatus(NodeStatus.Success);
		}
		// stop
		else if (action.getActionName().equals(StopAction)) {			
			game.stopChaser();
			action.setStatus(NodeStatus.Success);
		} 
		// wait 
		else if (action.getActionName().equals(WaitAction)) {
			waitActions.put(action, (System.currentTimeMillis() + (Integer)action.getExecutionParameters()[0]));
		}
	}

	/**
	 * Check if wait actions have completed. 
	 */
	public void onUpdate(ActionNode action) {
		if (waitActions.containsKey(action)) {
			if (System.currentTimeMillis() >= waitActions.get(action)) {
				action.setStatus(NodeStatus.Success);
			}
		}
	}
	
	public void abort(ActionNode action) {
	}
}
