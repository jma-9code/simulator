package fr.ensicaen.simulator.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.model.strategies.NullStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.tools.CaseInsensitiveMap;

@XmlSeeAlso({ ComponentIO.class, ComponentI.class, ComponentO.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Component implements Serializable {

	private static Logger log = LoggerFactory.getLogger(Component.class);

	@XmlAttribute
	@XmlID
	protected String uuid;

	@XmlTransient
	private String acronym;

	protected String name;

	protected HashMap<String, String> properties = new CaseInsensitiveMap();

	// delegate
	protected transient IStrategy strategy = new NullStrategy();

	// sub-component
	@XmlElement
	@XmlIDREF
	protected List<Component> childs = new ArrayList<>();

	public Component() {
		this.name = "default";
		this.uuid = UUID.randomUUID().toString();
	}

	public Component(String _name) {
		super();
		this.name = _name;
		this.uuid = UUID.randomUUID().toString();
		Context.getInstance().registerComponent(this, true);
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
			for (Component child : this.childs) {
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

	public List<Component> getChilds() {
		return this.childs;
	}

	public void setChilds(List<Component> components) {
		this.childs = components;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public String getAcronym() {
		if (acronym == null) {
			StringTokenizer token = new StringTokenizer(name, " ");
			StringBuilder build = new StringBuilder(4);
			while (token.hasMoreTokens()) {
				build.append(token.nextToken().substring(0, 1).toUpperCase());
			}
			acronym = build.toString();
		}
		return acronym;
	}

	@XmlTransient
	public IStrategy<? extends Component> getStrategy() {
		return this.strategy;
	}

	public void setStrategy(IStrategy<? extends Component> strategy) {
		this.strategy = strategy;
	}

	public String getInstanceName() {
		return this.name + "-" + this.uuid;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\n" + this.name + " - " + this.properties);
		for (Component c : this.childs) {
			sb.append("\t\n" + c.getName() + " - ");
			sb.append(c.getProperties());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childs == null) ? 0 : childs.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
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

	public abstract boolean isOutput();

	public abstract boolean isInput();

	public String getUuid() {
		return uuid;
	}

	/**
	 * Instancie le composant
	 */
	public void instanciate() {
		this.uuid = "c-" + UUID.randomUUID().toString();
		log.info(getInstanceName() + " instancied");
	}

	/**
	 * Recupere une liste des composants et du composant lui meme (sans
	 * doublon).
	 * 
	 * @return
	 */
	public List<Component> getAllTree() {
		List<Component> ret = new ArrayList<>();
		ret.add(this);
		for (Component c : getChilds()) {
			List<Component> childs = c.getAllTree();
			// retire les doublons
			for (Component c1 : childs) {
				if (!ret.contains(c1)) {
					ret.add(c1);
				}
			}
		}
		return ret;
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
		if (childs == null) {
			if (other.childs != null)
				return false;
		}
		else if (!childs.equals(other.childs))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		}
		else if (!properties.equals(other.properties))
			return false;
		if (strategy == null) {
			if (other.strategy != null)
				return false;
		}
		else if (!strategy.equals(other.strategy))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		}
		else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
