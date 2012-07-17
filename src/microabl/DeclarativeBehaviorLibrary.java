package microabl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import microabl.prototype.BehaviorPrototype;
import microabl.prototype.ConditionPrototype;
import microabl.prototype.StepPrototype;
import microabl.prototype.StepPrototype.StepModifier;
import microabl.prototype.Variable;
import microabl.wm.WME;

abstract public class DeclarativeBehaviorLibrary {

	public List<BehaviorPrototype> build() {
		return list();
	}
	
	public ArrayList<BehaviorPrototype> compile() {
		synthesizeIdentifiers();
		ArrayList<BehaviorPrototype> library = new ArrayList<BehaviorPrototype>();
		library.addAll(build());
		return library;
	}
	
	public void synthesizeIdentifiers() {
		Class<?> clazz = getClass();
		while(clazz != DeclarativeBehaviorLibrary.class) {
			Field[] fields = clazz.getDeclaredFields();
			for(Field f : fields) {
				try {
					f.setAccessible(true);
					if(f.get(this) == null) {
						// try to synthesize an instance from the field's name
						// TODO: decide if anything constructible should be synthesized or just subclasses of Ident
						Constructor<?> ctor = f.getType().getConstructor(String.class);
						Object inst = ctor.newInstance(f.getName());
						f.set(this, inst);
					}
				} catch (Exception e) {
					// leave this field alone
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	
	private abstract static class Ident {
		final public String name;
		public Ident(String name) {
			this.name = name.intern();
		};
		public String toString() {
			return "Ident("+name+")";
		}
	}
	
	protected static class Goal extends Ident { public Goal(String name) { super(name); } }
	protected static class Action extends Ident { public Action(String name) { super(name); } }
	protected static class Var<T> extends Ident { public Var(String name) { super(name); } }
	
	protected <T> List<T> list(T... args) {
		return Arrays.asList(args);
	}
	
	protected interface Applicable<T> {
		void apply(T target);
	}
	protected class StepApplicable implements Applicable<BehaviorPrototype> {
		public final StepPrototype step;
		public StepApplicable (StepPrototype step) {
			this.step = step;
		}
		public void apply(BehaviorPrototype behavior) {
			addStep(step, behavior);
		}
	}
	
	protected Applicable<StepPrototype> persistent = new Applicable<StepPrototype>() {
		public void apply(StepPrototype proto) {
			proto.setModifier(StepModifier.Persistent);
		}
	};
	
	protected Applicable<BehaviorPrototype> specificity(final int spec) {
		return new Applicable<BehaviorPrototype>() {
			public void apply(BehaviorPrototype behavior) {
				behavior.setSpecificity(spec);
			}
		};
	}
	
	protected Applicable<StepPrototype> priority(final int prio) {
		return new Applicable<StepPrototype>() {
			public void apply(StepPrototype step) {
				step.setPriority(prio);
			}
		};
	}
	
	
	protected StepApplicable with(final Applicable<StepPrototype> tweak, final StepApplicable holder) {
		return new StepApplicable(holder.step) {
			public void apply(BehaviorPrototype target) {
				tweak.apply(holder.step);
				super.apply(target);
			}
		};
	}
	
	protected Applicable<BehaviorPrototype> precondition(final ConditionPrototype... conds) {
		return new Applicable<BehaviorPrototype>() {
			public void apply(BehaviorPrototype behavior) {
				ArrayList<ConditionPrototype> arrayList = new ArrayList<ConditionPrototype>(conds.length);
				arrayList.addAll(list(conds));
				behavior.setPreconditions(arrayList);
			}
		};
	}
	
	protected <T> Variable var(Var<T> ident) {
		return new Variable(ident.name);
	}
	
	protected <T> Binding<T> bind(final Var<T> attribute, final Var<T> variable) {
		return new Binding<T>(attribute, variable);
	}
	
	protected ConditionPrototype wme(Class<? extends WME> clazz, Binding<?>... bindings) {
		ConditionPrototype proto = ConditionPrototype.createWMECondition(clazz);
		for(Binding<?> b : bindings) {
			proto.addBinding(b.attribute.name, b.variable.name);
		}
		return proto;
	}
	
	protected ConditionPrototype no_wme(Class<? extends WME> clazz, Binding<?>... bindings) {
		ConditionPrototype proto = ConditionPrototype.createNegation(clazz);
		for(Binding<?> b : bindings) {
			proto.addBinding(b.attribute.name, b.variable.name);
		}
		return proto;
	}
	
	protected ConditionPrototype mental(Class<? extends WME> clazz, String name, Object... parameters) {
		return ConditionPrototype.createMental(clazz).setMethodName(name).setMethodParameters(parameters);
	}
	
	protected StepApplicable action(Action action, Object... parameters) {
		StepPrototype step = StepPrototype.createAction(action.name).setParameters(parameters);
		return new StepApplicable(step);
	}
	
	protected StepApplicable subgoal(Goal goal, Object... parameters) {
		StepPrototype step = StepPrototype.createSubgoal(goal.name).setParameters(parameters);
		return new StepApplicable(step);
	}

	protected BehaviorPrototype sequential_behavior(Goal goal, Applicable<BehaviorPrototype>... applicables) {
		return applyAll(BehaviorPrototype.createSequential(goal.name), applicables);
	}
	
	protected BehaviorPrototype parallel_behavior(Goal goal, Applicable<BehaviorPrototype>... applicables) {
		return applyAll(BehaviorPrototype.createParallel(goal.name), applicables);
	}
	
	protected BehaviorPrototype initial_tree(Applicable<BehaviorPrototype>... applicables) {
		return applyAll(BehaviorPrototype.createParallel(Agent.INITIAL_GOAL), applicables);
	}
	
	private <T> T applyAll(T target, Applicable<T>... applicables) {
		for(Applicable<T> a : applicables) {
			a.apply(target);
		}
		return target;
	}
	
	private BehaviorPrototype addStep(StepPrototype step, BehaviorPrototype behavior) {
		if(behavior.getSteps() == null) {
			behavior.setSteps(new ArrayList<StepPrototype>());
		}
		behavior.getSteps().add(step);
		return behavior;
	}
	
	protected final class Binding<T> {
		final public Var<T> attribute;
		final public Var<T> variable;
		public Binding(Var<T> attribute, Var<T> variable) {
			this.attribute = attribute;
			this.variable = variable;
		}
		public String toString() {
			return attribute.name + " :: " + variable.name;
		}
	}
}