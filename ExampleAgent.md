# Chaser Agent #

The project includes an example agent defined in `src/chaseragent/ChaserAgent.java`. The source code includes original ABL syntax and equivalent MicroABL code.

```
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
  subgoal Wait(2000); // this suspends the execution of the behavior
  with (persistent) subgoal fire();
}*/
                 
steps = new ArrayList<StepPrototype>();
steps.add(StepPrototype.createAction(WaitAction).setParameters(new Object[] { 2000 })); 
steps.add(StepPrototype.createSubgoal("fire").setModifier(StepModifier.Persistent));
behaviorLibrary.add(BehaviorPrototype.createSequential("manageFiring").setSteps(steps)); 
                

/*
sequential behavior fire() {
  precondition {
    (PlayerWME locationX::playerX locationY::playerY)   
    (ChaserWME locationX::chaserX locationY::chaserY)   
  }

  act fire(chaserX, chaserY, playerX, playerY); 
  subgoal Wait(500); 
}*/
 
ArrayList<ConditionPrototype> preconditions = new ArrayList<ConditionPrototype>();
preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("x", "chaserX").addBinding("y", "chaserY"));
preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX").addBinding("y", "playerY"));
steps = new ArrayList<StepPrototype>();  
steps.add(StepPrototype.createAction(FireAction).setParameters(new Object[] { new Variable("chaserX"), new Variable("chaserY"), new Variable("playerX"), new Variable("playerY") }));  
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
preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("greaterThanSum").setMethodParameters( new Object[] { new Variable("chaserY"), new Variable("playerY"), Game.ChaserSpeed} ));
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
preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("lessThanDiff").setMethodParameters( new Object[] { new Variable("chaserY"), new Variable("playerY"), Game.ChaserSpeed} )); 
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
preconditions.add(ConditionPrototype.createWMECondition(ChaserWME.class).addBinding("x", "chaserX"));           preconditions.add(ConditionPrototype.createWMECondition(PlayerWME.class).addBinding("x", "playerX"));           preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("greaterThanSum").setMethodParameters(new Object[] { new Variable("chaserX"), new Variable("playerX"), Game.ChaserSpeed} )); 
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
preconditions.add(ConditionPrototype.createMental(ChaserWME.class).setMethodName("lessThanDiff").setMethodParameters(new Object[] { new Variable("chaserX"), new Variable("playerX"), Game.ChaserSpeed} )); 
steps = new ArrayList<StepPrototype>();  
steps.add(StepPrototype.createAction(MoveRightAction));  
behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(2).setSteps(steps).setPreconditions(preconditions));              


/*
sequential behavior move() {
  specificity 1;
  act stop();
  subgoal Wait(1000); 
} */ 
                
steps = new ArrayList<StepPrototype>();  
steps.add(StepPrototype.createAction(StopAction));  
steps.add(StepPrototype.createAction(WaitAction).setParameters(new Object[] { 1000 })); 
behaviorLibrary.add(BehaviorPrototype.createSequential("move").setSpecificity(1).setSteps(steps));                                              
```