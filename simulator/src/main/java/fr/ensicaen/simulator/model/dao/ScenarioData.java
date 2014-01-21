package fr.ensicaen.simulator.model.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
	private Map<String, Component> components = null;

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
	private Map<String, Class> link_strat_component = null;

	public ScenarioData() {

	}

	public ScenarioData(String _name, Context ctx) {
		setName(_name);
		components = ctx.getComponents();
		mediators = ctx.getMediators();
		link_strat_component = computelinks(new ArrayList<Component>(components.values()));
		startPoints = ctx.getStartPoints();
		events = ctx.getEvents();
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
	private static Map<String, Class> computelinks(List<Component> _components) {
		Map<String, Class> ret = new HashMap<>();
		for (Component c : _components) {
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
		result = prime * result + ((components == null) ? 0 : components.hashCode());
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
		if (components == null) {
			if (other.components != null)
				return false;
		}
		else if (!components.equals(other.components))
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
		return components;
	}

	public void setComponents(Map<String, Component> components) {
		this.components = components;
	}
}