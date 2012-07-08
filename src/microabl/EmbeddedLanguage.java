package microabl;

import java.util.ArrayList;

import microabl.prototype.BehaviorPrototype;
import microabl.prototype.ConditionPrototype;
import microabl.prototype.StepPrototype;
import microabl.prototype.StepPrototype.StepModifier;
import microabl.prototype.Variable;
import microabl.wm.WME;
/**
 * Utility methods for reducing the amount of characters needed to specify behaviors.
 * 
 * @see ChaserAgentEmbedded
 */
public class EmbeddedLanguage {
	
	
	public static ItemTweak<BehaviorPrototype,StepPrototype>
		with(	Tweak<StepPrototype> mod,
				final ItemTweak<BehaviorPrototype,StepPrototype> step) {
		mod.apply(step.get());
		return step;
	}
	
	private static BehaviorPrototype applyTweaks(BehaviorPrototype proto, Tweak<BehaviorPrototype>[] tweaks) {
		for(Tweak<BehaviorPrototype> t : tweaks) {
			t.apply(proto);
		}
		return proto;
	}
	
	private static void addStep(BehaviorPrototype proto, StepPrototype step) {
		if(proto.getSteps() == null) {
			proto.setSteps(arrayList(step));
		} else {
			proto.getSteps().add(step);
		}
	}
	
	public static BehaviorPrototype initial_tree(Tweak<BehaviorPrototype>... tweaks) {
		return parallel_behavior(Agent.INITIAL_GOAL, tweaks);
	}
	
	public static Tweak<StepPrototype> priority(final int prio) {
		return new Tweak<StepPrototype>() {
			void apply(StepPrototype proto) {
				proto.setPriority(prio);
			}
		};
	}
	
	public static Tweak<StepPrototype> persistent = new Tweak<StepPrototype>() {
		void apply(StepPrototype proto) {
			proto.setModifier(StepModifier.Persistent);
		}
	};
	
	public static Tweak<BehaviorPrototype> specificity(final int spec) {
		return new Tweak<BehaviorPrototype>() {
			void apply(BehaviorPrototype proto) {
				proto.setSpecificity(spec);
			}
		};
	}
	
	public static ItemTweak<BehaviorPrototype,StepPrototype> action(String actionName, Object... parameters) {
		final StepPrototype step = StepPrototype.createAction(actionName).setParameters(parameters);
		return new ItemTweak<BehaviorPrototype,StepPrototype>() {
			void apply(BehaviorPrototype proto) { addStep(proto, step); }
			StepPrototype get() { return step; }
		};
	}
	
	public static ItemTweak<BehaviorPrototype,StepPrototype> subgoal(String goalName) {
		final StepPrototype step = StepPrototype.createSubgoal(goalName);
		return new ItemTweak<BehaviorPrototype,StepPrototype>() {
			void apply(BehaviorPrototype proto) { addStep(proto, step); }
			StepPrototype get() { return step; }
		};
	}
	
	public static BehaviorPrototype sequential_behavior(String goalName, Tweak<BehaviorPrototype>... tweaks) {
		return applyTweaks(BehaviorPrototype.createSequential(goalName), tweaks);
	}
	
	public static BehaviorPrototype parallel_behavior(String goalName, Tweak<BehaviorPrototype>... tweaks) {
		return applyTweaks(BehaviorPrototype.createParallel(goalName), tweaks);
	}
	
	public static Tweak<BehaviorPrototype> precondition(final ConditionPrototype... conds) {
		return new Tweak<BehaviorPrototype>() {
			void apply(BehaviorPrototype proto) {
				proto.setPreconditions(arrayList(conds));
			}
		};
	}
	
	public static <T> ArrayList<T> arrayList(T... elements) {
		ArrayList<T> list = new ArrayList<T>(elements.length);
		for(T e : elements) {
			list.add(e);
		}
		return list;
	}

	
	public static Variable var(String name) {
		return new Variable(name);
	}
	
	public static Object[] vars(String... names) {
		Object[] arr = new Object[names.length];
		for(int i = 0; i < names.length; i++) {
			arr[i] = new Variable(names[i]);
		}
		return arr;
	}
	
	public static ConditionPrototype wme(Class<? extends WME> clazz, Binding... bindings) {
		ConditionPrototype proto = ConditionPrototype.createWMECondition(clazz);
		for(Binding b : bindings) {
			proto.addBinding(b.attribute, b.variable);
		}
		return proto;
	}
	
	public static ConditionPrototype mental(Class<? extends WME> clazz, String methodName, Object... methodParameters) {
		return ConditionPrototype.createMental(clazz).setMethodName(methodName).setMethodParameters(methodParameters);
	}
	
	public static Binding bind(final String attribute, final String variable) {
		Binding b = new Binding();
		b.attribute = attribute;
		b.variable = variable;
		return b;
	}
	
	public static class Binding {
		public String attribute;
		public String variable;
	}

	public static abstract class Tweak<T> {
		abstract void apply(T target);
	}

	public static abstract class ItemTweak<T,I> extends Tweak<T> {
		abstract I get();
	}
}

