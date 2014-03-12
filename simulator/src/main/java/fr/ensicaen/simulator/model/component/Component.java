package fr.ensicaen.simulator.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertiesPlus;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.model.strategies.NullStrategy;
import fr.ensicaen.simulator.simulator.Context;

@XmlSeeAlso({ ComponentIO.class, ComponentI.class, ComponentO.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Component implements Serializable {

	private static Logger log = LoggerFactory.getLogger(Component.class);

	/**
	 * Identifiant unique du composant
	 */
	@XmlAttribute
	@XmlID
	protected String uuid;

	/**
	 * Acronyme
	 */
	@XmlTransient
	private String acronym;

	/**
	 * Nom du composant
	 */
	protected String name;

	/**
	 * Type du composant
	 */
	protected int type;

	/**
	 * Propriétés du composant
	 */
	protected PropertiesPlus properties = new PropertiesPlus();

	/**
	 * Stratégie utilisée par le composant (pattern delegate)
	 */
	protected transient IStrategy strategy = new NullStrategy();

	/**
	 * Liste des composants enfants
	 */
	@XmlElement
	@XmlIDREF
	protected List<Component> childs = new ArrayList<>();

	@XmlAttribute
	@XmlIDREF
	protected Component parent = null;

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

	public Component(String _name, int _type) {
		this(_name);
		type = _type;
	}

	/**
	 * Renvoi le composant enfant correspondant au nom donné.
	 * 
	 * @param name
	 *            Nom du composant
	 * @return Le composant ou null.
	 */
	public <T extends Component> T getChild(String name, Class<T> type) {
		log.debug("Search child named " + name);

		if (name != null) {
			for (Component child : this.childs) {
				log.debug("Search child " + name + ", current " + child.getType());
				if ((name.equalsIgnoreCase(child.getName()) || name.equalsIgnoreCase(child.getAcronym()))
						&& child.getClass() == type) {
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

	public PropertiesPlus getProperties() {
		return this.properties;
	}

	public void setProperties(PropertiesPlus properties) {
		this.properties = properties;
	}

	public List<Component> getChilds() {
		return this.childs;
	}

	public void addChild(Component child) {
		child.parent = this;
		this.childs.add(child);
		buildChildMediator(child);
	}

	/**
	 * Method factory des mediateurs implicites (relation parent-enfant)
	 * 
	 * @param child
	 */
	private void buildChildMediator(Component child) {
		// auto create and register child mediator
		Mediator m = MediatorFactory.getInstance().getMediator(this, child);
		if (m == null) {
			log.error("Child mediator registration failed.");
			// Context.getInstance().registerMediator(m);
		}
	}

	/**
	 * Recursive function to re-organize components list.
	 * 
	 * @param components
	 * @return
	 */
	public static List<Component> organizeComponents(Collection<Component> components) {
		List<Component> ret = new ArrayList<>();
		ret.addAll(components);
		for (Component c : components) {
			List<Component> tmp = organizeComponents(c.getChilds());
			for (Component c1 : tmp) {
				if (!components.contains(c1)) {
					ret.add(c1);
				}
			}
		}
		return ret;
	}

	/**
	 * Recupere le "fils" du composant "pere" ayant un type preci.
	 * 
	 * @param parent
	 * @param type
	 * @return null si pas trouve
	 */
	public static Component getFirstChildType(Component parent, int type) {
		List<Component> comps = Component.organizeComponents(Arrays.asList(parent));
		for (Component c : comps) {
			if (type == c.getType()) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Retrieve the root component
	 * 
	 * @param c
	 * @param components
	 * @return
	 */
	public static Component getRoot(Component c) {
		// root find
		if (c.parent == null) {
			return c;
		}

		// find parent
		return getRoot(c.parent);
	}

	/**
	 * Permet de recuperer tout les composants racines de la liste
	 * 
	 * @param components
	 * @return
	 */
	public static Set<Component> retrieveAllRootComponents(List<Component> components) {
		Set<Component> root_comps = new HashSet<>();
		Component root_comp = null;
		for (Component c : components) {
			root_comp = getRoot(c);
			root_comps.add(root_comp);
		}
		return root_comps;
	}

	/**
	 * Search if the component contain child with specific type
	 * 
	 * @param component
	 * @param type
	 * @return null if the component not contain specific type component in his
	 *         childs
	 */
	public static Component containsType(Component component, int type) {

		if (component.getType() == type) {
			return component;
		}

		Component res = null;
		for (Component c : component.getChilds()) {
			res = containsType(c, type);
			if (res != null) {
				break;
			}
		}

		return res;
	}

	public void setChilds(List<Component> components) {
		this.childs = components;
	}

	public String getName() {
		return name;
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

		// récupération des définitions de propriété
		List<PropertyDefinition> propertyDefs = strategy.getPropertyDefinitions();

		if (propertyDefs != null) {
			// init
			for (PropertyDefinition def : propertyDefs) {

				// on écrase pas ...
				if (!properties.containsKey(def)) {
					properties.put(def.getKey(), def.getDefaultValue(), def.isRequired());
				}
			}
		}
	}

	public String getInstanceName() {
		return this.name + "-" + this.uuid;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\n" + this.name + " - " + this.properties);
		for (Component c : this.childs) {
			sb.append("\t\n" + c.getType() + " - ");
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
	// public Memento saveState() {
	// return new Memento(this.properties);
	// }
	//
	// /**
	// * Restitue son état depuis un "Memento"
	// *
	// * @param pMemento
	// */
	// public void restoreState(Memento pMemento) {
	// this.properties = pMemento.getProperties();
	// }

	public abstract boolean isOutput();

	public abstract boolean isInput();

	public String getUuid() {
		return uuid;
	}

	/**
	 * Instancie le composant
	 */
	public void instanciate() {
		// generate id
		this.uuid = "c-" + UUID.randomUUID().toString();
		log.info(getInstanceName() + " instancied");

		// child operation
		if (childs != null) {
			for (Component child : childs) {
				// build implicit mediator (child/parent)
				buildChildMediator(child);
				// set the parent pointer
				child.parent = this;
				// recursive call
				child.instanciate();
			}
		}
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
