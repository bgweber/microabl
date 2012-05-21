package abllite.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import abllite.abt.ABTRuntimeError;
import abllite.wm.WME;
 

public class ConditionPrototype {

	public enum ConditionType { WME, Negation, Mental }
  
	public enum Comparison { Equals, NEquals, eq, neq, gt, gte, lt, lte };
	 
	public class Test {
		private String property;
		private Object value;
		private Comparison comparison; 
		
		public Test(String property, Object value, Comparison comparison) {
			this.property = property;
			this.value = value;
			this.comparison = comparison; 
		}
	}

	private ConditionType type;
	private Class wmeClass;
	 
	private String wmeVariable = null;
	private HashMap<String, String> bindings = new HashMap<String, String>(); 
	private ArrayList<Test> tests = new ArrayList<Test>();

	// mental conditions
	private String methodName; 
	private Object[] methodParameters = new Object[0]; // considered literals, except for Variable instances 
		
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

	public static ConditionPrototype createNegation(Class wmeClass) {
		return new ConditionPrototype(ConditionType.Negation, wmeClass); 
	} 
  
	public static ConditionPrototype createMental(Class wmeClass) { 
		return new ConditionPrototype(ConditionType.Mental, wmeClass); 
	}

	public ConditionPrototype addBinding(String attribute, String parameter) {
		bindings.put(attribute, parameter);
		return this;
	}  

	public ConditionPrototype addTest(String property, Comparison comparison, Object value) {
		tests.add(new Test(property, value, comparison)); 
		return this;
	}   

	public ConditionPrototype setWMEVariable(String wmeVariable) {
		this.wmeVariable = wmeVariable;
		return this; 
	}  
	 
	public ConditionPrototype setMethodName(String methodName) {
		this.methodName = methodName;
		return this; 
	} 
	 
	public ConditionPrototype setMethodParameters(Object[] parameters) {
		this.methodParameters = parameters;
		return this;
	}     
	
	public String getMethodName() {
		return methodName;
	}

	public Object[] getMethodParameters() {
		return methodParameters;
	}
	
	public String getWMEVariable() {
		return wmeVariable; 
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
		
	public boolean testWME(WME wme, HashMap<String, Object> variables) {		

		for (Test test : tests) {
			Object wmeValue = wme.getAttribute(test.property);
 
			Object conditionValue = test.value;			
			if (conditionValue != null && conditionValue instanceof Variable) {
				conditionValue = variables.get(((Variable)conditionValue).getName()); 
			}

			switch (test.comparison) {
			case Equals:				
				if (!testEqual(wmeValue, conditionValue)) {
					return false; 
				}
				break;
			case NEquals: 				
				if (!testNEqual(wmeValue, conditionValue)) {
					return false; 
				}
				break;
			case eq:				
				if (!testEq(wmeValue, conditionValue)) {
					return false; 
				}
				break;
			case neq:				
				if (!testNeq(wmeValue, conditionValue)) {
					return false; 
				}
				break;
			default:				
				if (!test(wmeValue, conditionValue, test.comparison)) {
					return false; 
				}
				break;
			}
		}
		
		return true;
	}

	public boolean execute(HashMap<String, Object> variables) {
		
		// bind parameters 
		Object[] parameters = new Object[0];
		if (methodParameters != null && methodParameters.length > 0) {
			parameters = new Object[methodParameters.length];

			for (int index=0; index<methodParameters.length; index++) { 
				parameters[index] = (methodParameters[index] instanceof Variable) ? variables.get(((Variable)methodParameters[index]).getName()) : methodParameters[index];
			}
		}
		 
		// look up parameter classes 
		Class[] classes = new Class[parameters .length];			
		for (int i=0; i<parameters .length; i++) {
			classes[i] = parameters [i].getClass();
		}
 
		// invoke the method 
		try {
			Object result = wmeClass.getMethod(methodName, classes).invoke(null, parameters);
			if (result.equals(true)) {
				return true;
			}
			else if (result.equals(false)) {
				return false;
			}
			else {
				throw new ABTRuntimeError("Mental condition does not return a boolean result: " + methodName);
			}
		}
		catch (Exception e) {
			e.printStackTrace(); 
			throw new ABTRuntimeError("Mental condition failed: " + e.getMessage());
		}
	} 
 
	private final boolean testEq(Object wmeValue, Object conditionValue) {

		if (wmeValue != null && conditionValue != null && wmeValue instanceof Integer && conditionValue instanceof Integer) {
			return ((Integer)wmeValue).equals(conditionValue); 
		}
		else {
			return false;
		}
	}
  
	private final boolean testNeq(Object wmeValue, Object conditionValue) {

		if (wmeValue != null && conditionValue != null && wmeValue instanceof Integer && conditionValue instanceof Integer) {
			return !((Integer)wmeValue).equals(conditionValue); 
		}
		else {
			return false;
		}
	}

 	private final boolean test(Object wmeValue, Object conditionValue, Comparison comparison) {
		if (wmeValue == null || conditionValue == null) {
			return false;   
		} 
 
		Double wme = null; 
		if (wmeValue instanceof Integer) {
			wme = ((Integer)wmeValue).doubleValue();
		}
		else if (wmeValue instanceof Double) {
			wme = (Double)wmeValue; 
		}

		Double condition = null;
		if (conditionValue instanceof Integer) {
			condition = ((Integer)conditionValue).doubleValue();
		} 
		else if (conditionValue instanceof Double) {
			condition = (Double)conditionValue; 
		} 
 
		if (wme != null && condition != null) {
			 switch (comparison) {
			 case lt: 
				 return wme < condition; 
			 case lte:  
				 return wme <= condition; 
			 case gt: 
				 return wme > condition; 
			 case gte: 
				 return wme >= condition; 
			 }
		}
		
		return false;
	}

	private final boolean testEqual(Object wmeValue, Object conditionValue) {
		if (wmeValue == null) {
			return (conditionValue == null);
		}
		else {
			return wmeValue.equals(conditionValue);
		}
	}

	private final boolean testNEqual(Object wmeValue, Object conditionValue) {
		if (wmeValue == null) {
			return (conditionValue != null);
		}
		else {
			return !wmeValue.equals(conditionValue);
		}
	}
  
	public String toString() {
		if (type == ConditionType.WME) {
			StringBuffer result = new StringBuffer("WME Condition: " + wmeClass.getSimpleName()); 
			
			for (Test test : tests) {
				result.append("\n  test: " + test.property + " " + test.comparison + " " + test.value);
			}
			
			for (String variable : bindings.keySet()) {
				result.append("\n  bind: " + variable + " " + bindings.get(variable));
			}
			
			if (wmeVariable != null) {
				result.append("\n  result: " + wmeVariable);
			}
			
			return result.toString();
		}
		else if (type == ConditionType.Negation) { 
			StringBuffer result = new StringBuffer("WME Negation: " + wmeClass.getSimpleName()); 
			
			for (Test test : tests) {
				result.append("\n  test: " + test.property + " " + test.comparison + " " + test.value);
			}

			return result.toString();
		}
		
		return "Mental";
	}
}
