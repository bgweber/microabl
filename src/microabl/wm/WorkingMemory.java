package microabl.wm;

import java.util.HashMap;
import java.util.HashSet;
/**
 * Maintains lists of working memory elements (WMEs). 
 */
public class WorkingMemory {

	/** Maintains lists of WMEs indexed by WME class. **/
	private HashMap<Class<? extends WME>, HashSet<WME>> wmes = new HashMap<Class<? extends WME>, HashSet<WME>>();
 
	/**
	 * Adds a WME to working memory. 
	 * 
	 * Note: Sets are used in this implementation and duplicate additions are ignored.  
	 */
	public void addWME(WME wme) {
		Class wmeClass = wme.getClass();
		
		while (!wmeClass.equals(WME.class)) {
			if (!wmes.containsKey(wmeClass)) {
				wmes.put(wmeClass, new HashSet<WME>());
			}
			
			wmes.get(wmeClass).add(wme);
 		
			// continue to add the WME to super classes until the base WME class is reached. 
			wmeClass = wmeClass.getSuperclass();
		}		
	}

	/**
	 * Removes a WME from working memory. 
	 */
	public void removeWME(WME wme) {
		Class wmeClass = wme.getClass();
		 
		while (!wmeClass.equals(WME.class)) {
			if (wmes.containsKey(wmeClass)) {
				wmes.get(wmeClass).remove(wme);
			}
						
			wmeClass = wmeClass.getSuperclass();
		}		
	} 

	/**
	 * Returns all WMEs of the specified class. 
	 */
	public HashSet<WME> getWMEs(Class<? extends WME> wmeClass) {
		if (!wmes.containsKey(wmeClass)) {
			wmes.put(wmeClass, new HashSet<WME>());
		}

		return wmes.get(wmeClass);
	} 

	/**
	 * Prints everything in working memory. 
	 */
	public void dump() {
		System.out.println("Working Memory");
		
		for (Class<? extends WME> wmeClass : wmes.keySet()) {
			System.out.println("  " + wmeClass.getSimpleName() + ": " + wmes.get(wmeClass));
		}
	}
}
