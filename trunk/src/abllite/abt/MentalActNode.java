package abllite.abt;
/**
 * A node that invokes a Java method. 
 *
 * Mental acts are instantiated with a set of prototype parameters. These parameters
 * include literals and variables. Before this node is selected for expansion, the
 * ABT binds the prototype parameters to execution parameters by looking up 
 * behavior-scoped variables. 
 */
public class MentalActNode extends ABTNode {
	
	/** the class containing the method to invoke */
	private Class actionClass; 

	/** the name of the static method */
	private String methodName;
 
	/** parameters for invoking the method */ 
	private Object[] prototypeParameters; 
 
	/** result of the method invocation */ 
	private Object result; 
	
	/** an optional variable name for binding the result of the method invocation to a behavior variable */ 
	private String resultBinding;

	/** 
	 * Instantiates a mental act 
	 *  
	 * @param actionClass - class containing the method 
	 * @param methodName - name of the static method to invoke
	 * @param prototypeParameters - parameters passed to the Java method 
	 * @param resultBinding - optional parameter for binding invocation result to a behavior variable 
	 */
	public MentalActNode(Class actionClass, String methodName, Object[] prototypeParameters, String resultBinding) {
		this.actionClass = actionClass;
		this.methodName = methodName;
		this.prototypeParameters = prototypeParameters;
		this.resultBinding = resultBinding;
	}  
	
	/**
	 * Invokes the mental act. 
	 * 
	 * The node immediately succeeds upon completion of the method invocation. 
	 */
	public void execute(Object[] parameters) {

		// look up parameter classes 
		Class[] classes = new Class[parameters .length];			
		for (int i=0; i<parameters .length; i++) {
			classes[i] = parameters [i].getClass();
		}
  
		// invoke the method 
		try {
			result = actionClass.getMethod(methodName, classes).invoke(null, parameters);
		}
		catch (Exception e) {
			e.printStackTrace(); 
			throw new ABTRuntimeError("Mental act failed: " + e.getMessage());
		}
		
		setStatus(NodeStatus.Success);
	} 

	public void setResultBinding(String resultBinding) {
		this.resultBinding = resultBinding;
	}

	public String toString() { 
		return "MentalActNode: " + actionClass + "." + methodName + " [" + prototypeParameters.length + "] (" + nodeStatus + ") " + getPriority(); 
	}
 
	public Object[] getPrototypeParameters() {
		return prototypeParameters;
	}

	public String getResultBinding() {
		return resultBinding;
	}
 	 
	public Object getResult() {
		return result;
	}
}
