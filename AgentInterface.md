# Introduction #

A MicroABL agent interacts with an environment through actions, and perceives the world through objects added to working memory. Unlike ABL, MicroABL does not use sensors. Updates to working memory must not be performed while the `Agent.update()` method is invoked.

# Actions #

To create an ABL agent, it is necessary to provide an `ActionListener` as a parameter to the agent. This listener will be informed when the agent has selected actions to perform. An action listener must implement the following methods:

  * `public void execute(ActionNode action)`
    * Informs the listener that an action has been selected for execution.

  * `public void onUpdate(ActionNode action)`
    * Informs the listener that the action is executing during a decision cycle.

  * `public void abort(ActionNode action)`
    * Informs the listener that an executing action should be aborted.

# Working Memory #

Working memory stores the agent's perception of the world. It is a collection of working memory elements (WMEs), which are indexed by class. All WMEs must extend the `WME` base class and provide Java Bean style getters for attributes that need to be retrieved by ABL behaviors. The following operations can be performed on working memory:

  * `void addWME(WME wme)`
    * Adds a WME to working memory.

  * `void removeWME(WME wme)`
    * Removes a WME from working memory.

  * `HashSet<WME> getWMEs(Class<? extends WME> wmeClass)`
    * Returns all WMEs of the specified class.