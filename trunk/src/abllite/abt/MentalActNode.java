package abllite.abt;




public class MentalActNode extends ABTNode {
	
	private Class actionClass; 

	private String methodName;
 
	private Object[] parameters = new Object[] {}; 

	private Object result; 
	
	private String resultBinding;
	
	public MentalActNode(Class actionClass, String methodName, Object[] parameters, String resultBinding) {
		this.actionClass = actionClass;
		this.methodName = methodName;
		this.parameters = parameters;
		this.resultBinding = resultBinding;
	} 

	public void setResultBinding(String resultBinding) {
		this.resultBinding = resultBinding;
	}

	public String toString() { 
		return "MentalActNode: " + actionClass + "." + methodName + " [" + parameters.length + "] (" + nodeStatus + ") " + getPriority(); 
	}
 
	public Object[] getParameters() {
		return parameters;
	}
	
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
	
	public String getResultBinding() {
		return resultBinding;
	}
 	
	public Object getResult() {
		return result;
	}
}
