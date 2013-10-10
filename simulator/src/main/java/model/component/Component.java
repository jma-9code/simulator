package model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.mediator.Mediator;
import model.strategies.IStrategy;
import model.strategies.NullStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Component {
	
	private static Logger log = LoggerFactory.getLogger(Component.class);
	
	protected HashMap<String, String> properties = new HashMap<>();
	protected List<Component> components = new ArrayList<>();
	protected String name;
	protected IStrategy strategy = new NullStrategy();
	
	public Component(){
		
	}
	
	
	

	public Component(String _name) {
		name = _name;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\n" + name + " - " + properties);
		for (Component c : components){
			sb.append("\t\n" + c.getName() + " - ");
			sb.append(c.getProperties());
		}
		return sb.toString();
	}

	public IStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result
				+ ((strategy == null) ? 0 : strategy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Component other = (Component) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (strategy == null) {
			if (other.strategy != null)
				return false;
		} else if (!strategy.equals(other.strategy))
			return false;
		return true;
	}

	
	 /**
     * Contient la sauvegarde d'un état d'un objet.
     * Ses méthodes sont privées, afin que seul le "Createur"
     * accéde aux informations stockées
     */
    public class Memento {

        // Etat sauvegardé
    	protected HashMap<String, String> properties = new HashMap<>();
        
        private Memento(HashMap<String, String> _properties) {
        	properties = _properties;
        }
        
        /**
         * Retourne l'état sauvegardé
         * @return
         */
        private HashMap<String, String> getProperties() {
            return properties;
        }
    }
    
    /**
     * Sauvegarde son état dans un "Memento"
     * @return
     */
    public Memento saveState() {
        return new Memento(properties);
    }
    
    /**
     * Restitue son état depuis un "Memento"
     * @param pMemento
     */
    public void restoreState(Memento pMemento) {
        properties = pMemento.getProperties();
    }
}
