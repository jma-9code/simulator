package model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.strategies.IStrategy;
import model.strategies.NullStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.CaseInsensitiveMap;

public abstract class Component {

	private static Logger log = LoggerFactory.getLogger(Component.class);

	// attributes
	protected String name;
	protected HashMap<String, String> properties = new CaseInsensitiveMap();

	// delegate
	protected IStrategy<Component> strategy = new NullStrategy();

	// sub-component
	protected List<Component> components = new ArrayList<>();

	public Component() {
	}

	public Component(String _name) {
		this.name = _name;
	}

	/**
	 * Renvoi le composant enfant correspondant au nom donné.
	 * 
	 * @param name
	 *            Nom du composant
	 * @return Le composant ou null.
	 */
	public <T extends Component> T getChild(String name, Class<T> type) {
		if (name != null) {
			for (Component child : this.components) {
				if (name.equalsIgnoreCase(child.getName()) && child.getClass() == type) {
					return (T) child;
				}
			}
		}

		return null;
	}

	// shortcut
	public String getProperty(String key) {
		return this.properties.get(key);
	}

	public HashMap<String, String> getProperties() {
		return this.properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public List<Component> getComponents() {
		return this.components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IStrategy getStrategy() {
		return this.strategy;
	}

	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\n" + this.name + " - " + this.properties);
		for (Component c : this.components) {
			sb.append("\t\n" + c.getName() + " - ");
			sb.append(c.getProperties());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.components == null) ? 0 : this.components.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
		result = prime * result + ((this.strategy == null) ? 0 : this.strategy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Component other = (Component) obj;
		if (this.components == null) {
			if (other.components != null) {
				return false;
			}
		} else if (!this.components.equals(other.components)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!this.properties.equals(other.properties)) {
			return false;
		}
		if (this.strategy == null) {
			if (other.strategy != null) {
				return false;
			}
		} else if (!this.strategy.equals(other.strategy)) {
			return false;
		}
		return true;
	}

	/**
	 * Contient la sauvegarde d'un état d'un objet. Ses méthodes sont privées,
	 * afin que seul le "Createur" accéde aux informations stockées
	 */
	public class Memento {

		// Etat sauvegardé
		protected HashMap<String, String> properties = new HashMap<>();

		private Memento(HashMap<String, String> _properties) {
			this.properties = _properties;
		}

		/**
		 * Retourne l'état sauvegardé
		 * 
		 * @return
		 */
		private HashMap<String, String> getProperties() {
			return this.properties;
		}
	}

	/**
	 * Sauvegarde son état dans un "Memento"
	 * 
	 * @return
	 */
	public Memento saveState() {
		return new Memento(this.properties);
	}

	/**
	 * Restitue son état depuis un "Memento"
	 * 
	 * @param pMemento
	 */
	public void restoreState(Memento pMemento) {
		this.properties = pMemento.getProperties();
	}

}
