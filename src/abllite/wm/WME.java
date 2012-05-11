package abllite.wm;

import abllite.abt.ABTRuntimeError;

public class WME {

	public Object getAttribute(String attribute) {
		try { 
			if (attribute.length() == 0) {
				throw new ABTRuntimeError("Invalid attribute");
			}
  
			return getClass().getMethod("get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1)).invoke(this); 
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ABTRuntimeError(e.getLocalizedMessage());
		}
	}
  
}
