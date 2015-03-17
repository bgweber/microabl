# Introduction #

MicroABL supports a subset of ABL features. One of the main limitations is a lack of support for meta-behaviors and joint goals. An overview of some of ABL's semantics is provided in this [blog post](http://www.gamasutra.com/blogs/BenWeber/20120308/165151/ABL_versus_Behavior_Trees.php).

# Behaviors #

Behaviors are defined by the `BehaviorPrototype` class, which represent an ABL behavior. The following behavior types are supported:
  * **Sequential:** steps are added to the ABT one at a time.
  * **Parallel:** steps are added to the ABT all at once and pursued concurrently.

# Step Types #

Steps are defined by the `StepPrototype` class. These are prototype steps used to define the behavior library and are not added to the ABT. Priorities and modifiers can be assigned to all step types. The following step types are provided:
  * **Action:** performs a physcal action
    * stepName: name of the physical action
    * parameters: action parameters, specified as literals and Variables

  * **Subgoal:** creates a new sub goal with the specified parameters
    * stepName: name of the sub goal
    * parameters: goal parameters, specified as literals and Variables

  * **Spawngoal:** creates a new root goal with the specified parameters
    * stepName: name of the new root goal
    * parameters: goal parameters, specified as literals and Variables

  * **Wait Step:** suspended until wait conditions are met
    * waitConditions: conditions to wait on

  * **Fail Step:** immediately returns failure

  * **Succeed Step:** immediately returns success

  * **Mental Act:** invokes a Java method (blocks the execution of the ABT)
    * actionClass: name of the class that contains the method
    * stepName: name of the static method to invoke
    * parameters: method parameters, specified as literals and Variables
    * resultBinding: an optional parameter for binding the result to a behavior-scoped variable


# Modifiers #

Modifiers can be used to change the result of a step or to retry steps. The following modifiers are supported:
  * **None:** immediately returns the status of the child
  * **Persistent:** continues to perform the step regardless of step success or failure
  * **Ignore Failure:** returns success when the step fails or succeeds
  * **Persistent When Succeeds:** continues to perform the step when it succeeds
  * **Persistent When Fails:** continues to perform the step when it fails