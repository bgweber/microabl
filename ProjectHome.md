![http://i.imgur.com/GRJih.png](http://i.imgur.com/GRJih.png)


# Overview #

MicroABL is an interpreted version of a subset of the ABL reactive planning language, which provides a Java behavior tree framework.

A Behavior Language (ABL) is a reactive planning language developed to author the autonomous characters in the interactive drama Facade. In ABL, an agent is specified as a collection of reactive planning behaviors in a declarative language and compiled to a Java runtime. Documentation on ABL is available at the [ABL Wiki](http://abl.soe.ucsc.edu/index.php/Main_Page) and an introduction to authoring ABL agents is available at this [blog post](http://eis-blog.ucsc.edu/2012/02/getting-started-with-abl/).

ABL shares many similarities behavior tree (BT) systems. Generally, ABL provides more types of nodes and modifiers than current BT systems, but operates slightly differently. An overview of the differences between ABL and behaviors trees is available [here](http://www.gamasutra.com/blogs/BenWeber/20120308/165151/ABL_versus_Behavior_Trees.php).

MicroABL is a re-implementation of a subset of ABL functionality. Rather than providing a language for authoring agents, MicroABL focuses on the decision making data structure of ABL agents, known as the active behavior tree (ABT). Agents are specified as a collection of behaviors prototypes in Java. The goal of MicroABL is to provide a lightweight library for authoring ABL agents that provides a subset of the functionality of the full language, including support for spawning new goals, passing parameters to subgoals, and pursuing concurrent actions and goals.

# Content #

  * LanguageReference: an overview of the types of behaviors, steps, and modifiers supported by MicroABL
  * AgentInterface: an overview of how to interface an agent with an environment
  * ExampleAgent: an overview of a minimal MicroABL agent
  * EmbeddedLanguage: reducing the burden of authoring MicroABL agents


# Getting Started #

No binary releases are currently available. To get started, check out the project [source code](http://code.google.com/p/microabl/source/checkout). The source includes two example agents:

  * `testagent.Test`: A unit test for running MicroABL
  * `chaseragent.ChaserAgent`: A minimal example of hooking up MicroABL to an environment