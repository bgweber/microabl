package abllite.prototype;

import java.util.HashMap;


public class ConditionPrototype {

	public enum ConditionType { WME, Negation, Mental }

	private ConditionType type;
	private Class wmeClass;
	 
	private HashMap<String, String> bindings = new HashMap<String, String>(); 
	private String wmeProperty = null;
	
	public ConditionPrototype(ConditionType type) {
		this.type = type;
	} 

	public ConditionPrototype(ConditionType type, Class wmeClass) {
		this(type);
		this.wmeClass = wmeClass; 		
	}
  
	public static ConditionPrototype createWMECondition(Class wmeClass) {
		return new ConditionPrototype(ConditionType.WME, wmeClass); 
	}
	
	public ConditionPrototype addBinding(String attribute, String parameter) {
		bindings.put(attribute, parameter);
		return this;
	}  
 
	public ConditionPrototype setWMEProperty(String wmeProperty) {
		this.wmeProperty = wmeProperty;
		return this;
	} 

	public String getWMEProperty() {
		return wmeProperty; 
	}
	
	public Class getWMEClass() {
		return wmeClass;
	}
	
	public boolean isWMECheck() {
		return type == ConditionType.WME;
	}
	
	public boolean isNegationCheck() {
		return type == ConditionType.Negation;
	}
	
	public HashMap<String, String> getBindings() {
		return bindings;
	}
	
//	public StepPrototype(StepType stepType, String stepName) {
//		this(stepType);
//		this.stepName = stepName;
//	} 
//   
//	public static StepPrototype createSubgoal(String goalName) {
//		return new StepPrototype(StepType.Subgoal, goalName);
//	}
 
	
	
	
    // < <= = >= >	
	
//	private Class<? extends WME> wmeClass; 
//	private Map<String, Object> conditionChecks;	// todo method rather than string? 
//	private Map<String, Object> negationChecks;	// todo method rather than string? 
//	private Map<String, Object> bindings;	 

	public String toString() {
		if (type == ConditionType.WME) {
			return wmeClass.getSimpleName() + " " + bindings;
		}
		
		return "Mental";
	}
}
