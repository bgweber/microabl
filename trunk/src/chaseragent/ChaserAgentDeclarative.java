package chaseragent;

import java.util.ArrayList;
import java.util.List;

import microabl.DeclarativeBehaviorLibrary;
import microabl.prototype.BehaviorPrototype;
import microabl.prototype.ConditionPrototype;

public class ChaserAgentDeclarative extends ChaserAgent {

	public ChaserAgentDeclarative(Game game) {
		super(game);
	} 
	
	private class ChaserBehaviorLibrary extends DeclarativeBehaviorLibrary {
		Action Fire, Stop, Log, Wait;
		Action MoveLeft, MoveRight, MoveUp, MoveDown;
		// pseudo-keyword example
		protected StepApplicable log(Object... args) {
			return action(Log, args);
		}
		// native conditions
		ConditionPrototype greaterThanSum(Object... args) {
			return mental(ChaserWME.class,"greaterThanSum", args);
		}
		ConditionPrototype lessThanDiff(Object... args) {
			return mental(ChaserWME.class,"lessThanDiff", args);
		}
	}
	
	public ArrayList<BehaviorPrototype> getBehaviorLibrary() {
		return new ChaserBehaviorLibrary() {
			
			Goal manageFiring, manageMovement, fire, move;
			Var<Integer> playerX, playerY, chaserX, chaserY, x, y;
			
			@SuppressWarnings("unchecked")
			public List<BehaviorPrototype> build() {
				
				return list(
						
					initial_tree(
						with(priority(3), log("Starting Chaser Agent")),
						with(priority(2), subgoal(manageFiring)),
						with(priority(1), subgoal(manageMovement))),
						
					sequential_behavior(manageFiring,
						action(Wait, 2000),
						with(persistent, subgoal(fire))),
					
					sequential_behavior(fire,
						precondition(
							wme(ChaserWME.class,bind(x,chaserX),bind(y,chaserY)),
							wme(PlayerWME.class,bind(x,playerX),bind(y,playerY))),
						action(Fire, var(chaserX), var(chaserY), var(playerX), var(playerY)),
						action(Wait, 500)),

					sequential_behavior(manageMovement,
						with(persistent, subgoal(move))),
					
					sequential_behavior(move,
						precondition(
							wme(PlayerWME.class,bind(y,playerY)),
							wme(ChaserWME.class,bind(y,chaserY)),
							greaterThanSum(var(chaserY), var(playerY), Game.ChaserSpeed)),
						specificity(3),
						action(MoveUp)),
						
					sequential_behavior(move,
							precondition(
								wme(PlayerWME.class,bind(y,playerY)),
								wme(ChaserWME.class,bind(y,chaserY)),
								lessThanDiff(var(chaserY), var(playerY), Game.ChaserSpeed)),
							specificity(3),
							action(MoveDown)),
					
					sequential_behavior(move,
							precondition(
								wme(PlayerWME.class,bind(x,playerX)),
								wme(ChaserWME.class,bind(x,chaserX)),
								greaterThanSum(var(chaserX), var(playerX), Game.ChaserSpeed)),
							specificity(2),
							action(MoveLeft)),
							
					sequential_behavior(move,
							precondition(
								wme(PlayerWME.class,bind(x,playerX)),
								wme(ChaserWME.class,bind(x,chaserX)),
								lessThanDiff(var(chaserX), var(playerX), Game.ChaserSpeed)),
							specificity(2),
							action(MoveRight)),
						
					sequential_behavior(move,
						specificity(1),
						action(Stop),
						action(Wait, 1000))
						
				);
			}
		}.compile();
	}
}
