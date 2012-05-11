package abllite.wm;

import java.util.HashMap;
import java.util.HashSet;

public class WorkingMemory {
	 
	private HashMap<Class, HashSet<WME>> wmes = new HashMap<Class, HashSet<WME>>();

	public void addWME(WME wme) {
		Class wmeClass = wme.getClass();
		
		while (!wmeClass.equals(WME.class)) {
			if (!wmes.containsKey(wmeClass)) {
				wmes.put(wmeClass, new HashSet<WME>());
			}
			
			wmes.get(wmeClass).add(wme);
		
			
			wmeClass = wmeClass.getSuperclass();
		}		
	}

	public void removeWME(WME wme) {
		Class wmeClass = wme.getClass();
		 
		while (!wmeClass.equals(WME.class)) {
			if (wmes.containsKey(wmeClass)) {
				wmes.get(wmeClass).remove(wme);
			}
						
			wmeClass = wmeClass.getSuperclass();
		}		
	} 
	
	public HashSet<WME> getWMEs(Class<? extends WME> wmeClass) {
		if (!wmes.containsKey(wmeClass)) {
			wmes.put(wmeClass, new HashSet<WME>());
		}

		return wmes.get(wmeClass);
	} 

	public void dump() {
		for (Class wmeClass : wmes.keySet()) {
			System.out.println(wmeClass.getSimpleName() + ": " + wmes.get(wmeClass));
		}
	}
}
