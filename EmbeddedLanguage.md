# Embedded Language #

[Adam Smith](http://users.soe.ucsc.edu/~amsmith/) has provided a collection of utility methods that greatly reduce the amount of syntax needed to author MicroABL agents. The methods are defined in `src/microabl/EmbeddedLanguage.java` and demonstrated in `src/chaseragent/ChaserAgentEmbedded.java`. This agent code shows a comparison of the embedded language and the original ABL syntax:

```
/*
initial_tree {
  with (priority 3) mental_act {
    System.out.println("Starting Chaser Agent");
  }
	     	
  with (priority 2) subgoal manageFiring();
  with (priority 1) subgoal manageMovement();
}*/

initial_tree(
  with(priority(3), action(LogAction, "Starting Chaser Agent")),
  with(priority(2), subgoal(ManageFiring)),
  with(priority(1), subgoal(ManageMovement))),

					
/*
sequential behavior manageFiring() {
  subgoal Wait(2000); // this suspends the execution of the behavior
  with (persistent) subgoal fire();
}*/
		 
sequential_behavior(ManageFiring,
  action(WaitAction, 2000),
  with(persistent, subgoal(Fire))),
						

/*
sequential behavior fire() {
  precondition {
    (PlayerWME locationX::playerX locationY::playerY)	
    (ChaserWME locationX::chaserX locationY::chaserY)   
  }

  act fire(chaserX, chaserY, playerX, playerY);	
  subgoal Wait(500); 
}*/
 
sequential_behavior(Fire,
  precondition(			     
    wme(ChaserWME.class,bind(x,chaserX),bind(y,chaserY)),			
    wme(PlayerWME.class,bind(x,playerX),bind(y,playerY))),
  action(FireAction, vars(chaserX, chaserY, playerX, playerY)),
  action(WaitAction, 500)),
 		

/*
sequential behavior manageMovement() { 
  with (persistent) subgoal move();
}*/ 
 
sequential_behavior(ManageMovement,
  with(persistent, subgoal(Move))),
		

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
 
sequential_behavior(Move,
  precondition(
    wme(PlayerWME.class,bind(y,playerY)),
    wme(ChaserWME.class,bind(y,chaserY)),				  
    mental(ChaserWME.class,"greaterThanSum", var(chaserY), var(playerY), Game.ChaserSpeed)),
  specificity(3),
  action(MoveUpAction)),
				 

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

sequential_behavior(Move,
  precondition(
    wme(PlayerWME.class,bind(y,playerY)),
    wme(ChaserWME.class,bind(y,chaserY)),
    mental(ChaserWME.class,"lessThanDiff", var(chaserY), var(playerY), Game.ChaserSpeed)),
  specificity(3),
  action(MoveDownAction)),
				

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

sequential_behavior(Move,
  precondition(
    wme(PlayerWME.class,bind(x,playerX)),						 
    wme(ChaserWME.class,bind(x,chaserX)),
    mental(ChaserWME.class,"greaterThanSum", var(chaserX), var(playerX), Game.ChaserSpeed)),
  specificity(2),
  action(MoveLeftAction)),
						

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
 
sequential_behavior(Move,
  precondition(
    wme(PlayerWME.class,bind(x,playerX)),
    wme(ChaserWME.class,bind(x,chaserX)),
    mental(ChaserWME.class,"lessThanDiff", var(chaserX), var(playerX), Game.ChaserSpeed)),
  specificity(2),
  action(MoveRightAction)),
					

/*
sequential behavior move() {
  specificity 1;
  act stop();
  subgoal Wait(1000); 
} */ 
		
sequential_behavior(Move,
  specificity(1),
  action(StopAction),
  action(WaitAction, 1000))
```