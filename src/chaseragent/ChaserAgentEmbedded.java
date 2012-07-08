package chaseragent;

import java.util.ArrayList;

import microabl.prototype.BehaviorPrototype;

import static microabl.EmbeddedLanguage.*;

public class ChaserAgentEmbedded extends ChaserAgent {

	public ChaserAgentEmbedded(Game game) {
		super(game);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<BehaviorPrototype> getBehaviorLibrary() {
		String ManageFiring = "ManageFiring";
		String ManageMovement = "ManageMovement";
		String Fire = "Fire";
		String Move = "Move";
		String x = "x";
		String y = "y";
		String chaserX = "chaserX";
		String chaserY = "chaserY";
		String playerX = "playerX";
		String playerY = "playerY";

		return arrayList(
				
				initial_tree(
					with(priority(3), action(LogAction, "Starting Chaser Agent")),
					with(priority(2), subgoal(ManageFiring)),
					with(priority(1), subgoal(ManageMovement))),
					
				sequential_behavior(ManageFiring,
					action(WaitAction, 2000),
					with(persistent, subgoal(Fire))),
				
				sequential_behavior(Fire,
					precondition(
						wme(ChaserWME.class,bind(x,chaserX),bind(y,chaserY)),
						wme(PlayerWME.class,bind(x,playerX),bind(y,playerY))),
					action(FireAction, vars(chaserX, chaserY, playerX, playerY)),
					action(WaitAction, 500)),

				sequential_behavior(ManageMovement,
					with(persistent, subgoal(Move))),
				
				sequential_behavior(Move,
					precondition(
						wme(PlayerWME.class,bind(y,playerY)),
						wme(ChaserWME.class,bind(y,chaserY)),
						mental(ChaserWME.class,"greaterThanSum", var(chaserY), var(playerY), Game.ChaserSpeed)),
					specificity(3),
					action(MoveUpAction)),
					
				sequential_behavior(Move,
						precondition(
							wme(PlayerWME.class,bind(y,playerY)),
							wme(ChaserWME.class,bind(y,chaserY)),
							mental(ChaserWME.class,"lessThanDiff", var(chaserY), var(playerY), Game.ChaserSpeed)),
						specificity(3),
						action(MoveDownAction)),
				
				sequential_behavior(Move,
						precondition(
							wme(PlayerWME.class,bind(x,playerX)),
							wme(ChaserWME.class,bind(x,chaserX)),
							mental(ChaserWME.class,"greaterThanSum", var(chaserX), var(playerX), Game.ChaserSpeed)),
						specificity(2),
						action(MoveLeftAction)),
						
				sequential_behavior(Move,
						precondition(
							wme(PlayerWME.class,bind(x,playerX)),
							wme(ChaserWME.class,bind(x,chaserX)),
							mental(ChaserWME.class,"lessThanDiff", var(chaserX), var(playerX), Game.ChaserSpeed)),
						specificity(2),
						action(MoveRightAction)),
					
				sequential_behavior(Move,
					specificity(1),
					action(StopAction),
					action(WaitAction, 1000))
				
		);
	}		
}
