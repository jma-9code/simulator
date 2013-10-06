package model.memento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.component.Component;

/**
 * Conserve les "Memento".
 * Retourne un "Memento" conservé
 */
public class Guardian {
	private static Logger log = LoggerFactory.getLogger(Guardian.class);

    private HashMap<Component, List<Component.Memento>> components;
    
    private Guardian(){
    	components = new HashMap<>();
    }
    
    /**
     * Ajouter un "Memento" à la liste d'un composant
     * @param pMemento
     */
    public void addMemento(Component c, Component.Memento pMemento) {
    	if (components.containsKey(c)){
    		log.debug("Ajout d'un memento");
    		components.get(c).add(pMemento);
    	}
    	else{
    		log.debug("Ajout d'un nouveau composant dans le memento");
    		//creation du composant dans le gardien
    		ArrayList d = new ArrayList<>();
    		d.add(pMemento);
    		components.put(c, d);
    	}
    }
    
    /**
     * Retourne le "Memento" correspondant à l'index
     * @param pIndex
     * @return
     */
    public Component.Memento getMemento(Component c,int pIndex){
    	if (components.containsKey(c)){
    		return components.get(c).get(pIndex);
    	}
        return null;
    }
    
    private static class GuardianHolder
	{		
		/** Instance unique non préinitialisée */
		private final static Guardian instance = new Guardian();
	}
	
	/** Point d'accès pour l'instance unique du singleton */
	public static Guardian getInstance()
	{
		return GuardianHolder.instance;
	}
}