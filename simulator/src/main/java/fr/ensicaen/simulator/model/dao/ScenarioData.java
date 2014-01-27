package fr.ensicaen.simulator.model.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.EventMapAdapter;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.StartPoint;

/**
 * Classe conteneur permettant de stocker l'ensemble des donnees constituants un
 * scenario.
 * 
 * @author JM
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ScenarioData implements Serializable {

	private static final long serialVersionUID = -9088311987427401818L;
	@XmlAttribute
	private String name;

	@XmlElement
	@XmlElementWrapper
	private Map<String, Component> allComponents = null;

	@XmlIDREF
	@XmlElement
	@XmlElementWrapper
	private Collection<Component> rootComponents = null;

	@XmlElement
	@XmlElementWrapper
	private List<Mediator> mediators = null;

	@XmlElement
	@XmlElementWrapper
	private Queue<StartPoint> startPoints = null;

	@XmlJavaTypeAdapter(EventMapAdapter.class)
	@XmlElement
	private Map<String, List<IOutput>> events = null;

	/**
	 * Permet de faire le lien entre un composant et sa strategie Key=uuid
	 * component value=related strategy
	 */
	@XmlElement
	@XmlElementWrapper
	private Map<String, Class> link_strat_component = null;

	/* BEGIN GUI */

	@XmlElement
	@XmlElementWrapper
	private Map<String, Object> uiData = null;

	/* END GUI */

	public ScenarioData() {
	}

	public ScenarioData(String _name, Context ctx, Map<String, Object> uiData) {
		setName(_name);

		// root components
		this.rootComponents = ctx.getComponents().values();

		// all = root + childs components
		Map<String, Component> tmp = new HashMap<>();
		List<Component> c1 = organizeComponents(ctx.getComponents().values());
		for (Component c : c1)
			tmp.put(c.getUuid(), c);
		this.allComponents = tmp;

		this.mediators = ctx.getMediators();
		this.link_strat_component = computelinks(new ArrayList<Component>(allComponents.values()));
		this.startPoints = ctx.getStartPoints();
		this.events = ctx.getEvents();
		this.uiData = uiData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Recupere le nom des strategies associees aux composants
	 * 
	 * @param _components
	 * @return
	 */
	private static Map<String, Class> computelinks(Collection<Component> _components) {
		Map<String, Class> ret = new HashMap<>();
		for (Component c : _components) {
			if (c.getStrategy() != null)
				ret.put(c.getUuid(), c.getStrategy().getClass());
		}
		return ret;
	}

	public List<Mediator> getMediators() {
		return mediators;
	}

	public void setMediators(List<Mediator> mediators) {
		this.mediators = mediators;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allComponents == null) ? 0 : allComponents.hashCode());
		result = prime * result + ((link_strat_component == null) ? 0 : link_strat_component.hashCode());
		result = prime * result + ((mediators == null) ? 0 : mediators.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ScenarioData other = (ScenarioData) obj;
		if (allComponents == null) {
			if (other.allComponents != null)
				return false;
		}
		else if (!allComponents.equals(other.allComponents))
			return false;
		if (link_strat_component == null) {
			if (other.link_strat_component != null)
				return false;
		}
		else if (!link_strat_component.equals(other.link_strat_component))
			return false;
		if (mediators == null) {
			if (other.mediators != null)
				return false;
		}
		else if (!mediators.equals(other.mediators))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Recursive function to re-organize components.
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

	public Queue<StartPoint> getStartPoints() {
		return startPoints;
	}

	public void setStartPoints(Queue<StartPoint> startPoints) {
		this.startPoints = startPoints;
	}

	public Map<String, List<IOutput>> getEvents() {
		return events;
	}

	public void setEvents(Map<String, List<IOutput>> events) {
		this.events = events;
	}

	public Map<String, Class> getLink_strat_component() {
		return link_strat_component;
	}

	public void setLink_strat_component(Map<String, Class> link_strat_component) {
		this.link_strat_component = link_strat_component;
	}

	public Map<String, Component> getComponents() {
		return allComponents;
	}

	public void setComponents(Map<String, Component> components) {
		this.allComponents = components;
	}

	public Map<String, Object> getUiData() {
		return uiData;
	}

	public void setUiData(Map<String, Object> uiData) {
		this.uiData = uiData;
	}

	public Map<String, Component> getRootComponents() {
		Map<String, Component> map = new HashMap<String, Component>();
		for (Component c : rootComponents) {
			map.put(c.getName(), c);
		}
		return map;
	}

}