package microabl.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import microabl.abt.ABTRuntimeError;
import microabl.wm.WME;
/**
 * Represents a set of ABL conditions. Conditions perform checks on elements
 * in working memory (WMEs). 
 *
 * The following condition types are supported: 
 *   WME - checks for the existence of a WME in working memory that meets a set of tests
 *     wmeClass: the WME class to perform the condition test on 
 *     tests: condition tests to perform
 *     bindings: optional list of attributes to bind to variables 
 *     wmeVariable: an optional variable to bind a retrieved WME 
 *   
 *   Negation - checks for the lack of a WME in working memory that meets a set of tests
 *     wmeClass: the WME class to perform the condition test on 
 *     tests: condition tests to perform
 *     
 *   Mental - invokes a static method that returns a boolean result
 *     wmeClass: the WME class to use for method look up. 
 *     methodName: the name of the static method to invoke.
 *     methodParameters: a list of parameters to provide the WME method. 
 *   
 * The following comparison types are supported: 
 *   Equals - invokes the equals method on the attribute,
 *   NEquals - invokes the equals method on the attribute, returns true if values are non-equal 
 *   eq  - ==  (supports int type only)
 *   neq - != (supports int type only)
 *   gt  - >  (int or double type) 
 *   gte - >= (int or double type)
 *   lt  - <  (int or double type)
 *   lte - <= (int or double type) 
 */
public class ConditionPrototype {

	/** the types of condition checks */
	public enum ConditionType { WME, Negation, Mental }

	/** comparison types in a condition test */ 
	public enum Comparison { Equals, NEquals, eq, neq, gt, gte, lt, lte };

	/**
	 * Struct for a single condition test. (e.g. X > 10) 
	 */
	public class Test {
	
		/** name of the WME attribute being tested */
		private String attribute;
		
		/** value the attribute is being compared to */
		private Object value;
		
		/** type of comparison */
 		private Comparison comparison; 
		
		public Test(String attribute, Object value, Comparison comparison) {
			this.attribute = attribute;
			this.value = value;
			this.comparison = comparison; 
		}
	}

	/** the condition type (see definitions in class header) */ 
	private ConditionType type;

	/** the WME class the condition test operates on */ 
	private Class wmeClass;
	
	/** specifies if the WME should be bound to a behavior variable (for WME condition types only) */
	private String wmeVariable = null;

	/** bindings for WME attributes to behavior variables (for WME condition types only) */ 
	private HashMap<String, String> bindings = new HashMap<String, String>(); 
	
	/** list of condition tests */ 
	private ArrayList<Test> tests = new ArrayList<Test>();

	/** name of the WME method to invoke (for mental conditions only) **/ 
	private String methodName; 
	
	/** parameters of the WME method to invoke (for mental conditions only) **/ 
	private Object[] methodParameters = new Object[0]; // considered literals, except for Variable instances 
		
	private ConditionPrototype(ConditionType type) {
		this.type = type;
	} 

	private ConditionPrototype(ConditionType type, Class wmeClass) {
		this(type);
		this.wmeClass = wmeClass; 		
	}
 
	/**
	 * Instantiates a WME condition. Checks for the existence of a WME with the
	 * specified conditions, with options to bind attributes and the WME to 
	 * behavior-scoped variables. 
	 * 
	 * @param wmeClass - the WME class to test 
	 */
	public static ConditionPrototype createWMECondition(Class wmeClass) {
		return new ConditionPrototype(ConditionType.WME, wmeClass); 
	} 

	/**
	 * Instantiates a WME negation condition. Checks for a lack of a WME with 
	 * the specified contiions. 
	 * 
	 * @param wmeClass - the WME class to test 
	 */
	public static ConditionPrototype createNegation(Class wmeClass) {
		return new ConditionPrototype(ConditionType.Negation, wmeClass); 
	} 
  
	/**
	 * Instantiates a mental condition. Invokes a Java method, which must 
	 * return a boolean result. This invocation blocks the execution of the ABT.
	 * 
	 * @param wmeClass - the WME class to invoke. 
	 */
	public static ConditionPrototype createMental(Class wmeClass) { 
		return new ConditionPrototype(ConditionType.Mental, wmeClass); 
	}
	
	/**
	 * Adds a binding that maps the WME attribute to the behavior-scoped variable. 
	 */
	public ConditionPrototype addBinding(String attribute, String variable) {
		bindings.put(attribute, variable);
		return this;
	}  

	/**
	 * Adds a test to the condition. For WME and negation conditions. 
	 */
	public ConditionPrototype addTest(String attribute, Comparison comparison, Object value) {
		tests.add(new Test(attribute, value, comparison)); 
		return this; 
	}   

	/**
	 * Sets the variable to bind the for a WME condition test. 
	 */
	public ConditionPrototype setWMEVariable(String wmeVariable) {
		this.wmeVariable = wmeVariable;
		return this; 
	}  

	/**
	 * Sets the method to invoke for a mental condition. 
	 */
	public ConditionPrototype setMethodName(String methodName) {
		this.methodName = methodName;
		return this; 
	} 
	 
	/**
	 * Sets the method parameters for a mental condition. 
	 */
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
 
	/**
	 * Invokes a mental condition. Mental condition checks invoke Java methods 
	 * and block the execution of the ABT. 
	 * 
	 * @param variables - list of behavior variables. 
	 * @return true if the condition succeeds 
	 */
	public boolean execute(HashMap<String, Object> variables) {
		
		// bind parameters to behavior variables 
		Object[] parameters = new Object[0];
		if (methodParameters != null && methodParameters.length > 0) {
			parameters = new Object[methodParameters.length];

			for (int index=0; index<methodParameters.length; index++) { 
				parameters[index] = (methodParameters[index] instanceof Variable) ? 
						variables.get(((Variable)methodParameters[index]).getName()) : methodParameters[index];
			}
		}
		 
		// look up parameter classes 
		Class[] classes = new Class[parameters .length];			
		for (int i=0; i<parameters.length; i++) {
			classes[i] = parameters[i].getClass();
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
	  
	/** 
	 * Checks if the given WME meets the condition tests. Tests conditions for WME and negation conditions. 
	 * 
	 * @param wme - the wme instance to test 
	 * @param variables - list of behavior variables. 
	 * @return true if the WME meets the condition tests. 
	 */
	public boolean testWME(WME wme, HashMap<String, Object> variables) {		

		for (Test test : tests) { 
			
			// look up the WME attribute
			Object wmeValue = wme.getAttribute(test.attribute);
 
			// retrieve the condition value. If not a literal, look up the behavior variable. 
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
 
		// cast the wme attribute to a double 
		Double wme = null; 
		if (wmeValue instanceof Integer) {
			wme = ((Integer)wmeValue).doubleValue();
		}
		else if (wmeValue instanceof Double) {
			wme = (Double)wmeValue; 
		}

		// cast the condition value to a double 
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
  
	public String toString() {
		if (type == ConditionType.WME) {
			StringBuffer result = new StringBuffer("WME Condition: " + wmeClass.getSimpleName()); 
			
			for (Test test : tests) {
				result.append("\n  test: " + test.attribute + " " + test.comparison + " " + test.value);
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
				result.append("\n  test: " + test.attribute + " " + test.comparison + " " + test.value);
			}

			return result.toString();
		}
		else { 	 
			return "Mental condition: " + wmeClass + "." + methodName;
		}		
	}
}
