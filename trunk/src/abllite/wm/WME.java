package abllite.wm;

import abllite.abt.ABTRuntimeError;
/**
 * Base class for objects added to working memory. 
 */
public class WME {

	/**
	 * Invokes the getter for the given attribute. 
	 * Note: Uses Java bean naming conventions. (e.g. "name" invokes getName() ) 
	 * 
	 * Throw an error if there is not a getter for the specified attribute. 
	 * 
	 * @return the getter result
	 */
	public Object getAttribute(String attribute) {
		try { 
			if (attribute.length() == 0) {
				throw new ABTRuntimeError("Invalid attribute");
			}
  
			return getClass().getMethod("get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1)).invoke(this); 
		}
		catch (Exception e) {
			throw new ABTRuntimeError("Invalid attribute: '" + attribute + "' for WME: " + this.getClass().getSimpleName());
		}
	}  
}
