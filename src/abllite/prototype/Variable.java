package abllite.prototype;

public class Variable { 

	private String name;
	 
	public Variable(String name) {
		this.name = name; 
	}
	
	public String getName() {
		return name; 
	}
	 
	public String toString() {
		return "Variable(" + name + ")";
	} 
}
