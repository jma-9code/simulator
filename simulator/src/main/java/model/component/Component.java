package model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.mediator.Mediator;
import model.strategies.IStrategy;
import model.strategies.NullStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;

public abstract class Component {
	
	private static Logger log = LoggerFactory.getLogger(Component.class);
	
	protected HashMap<String, String> properties = new HashMap<>();
	protected List<Mediator> mediators = new ArrayList<>();
	protected List<Component> components = new ArrayList<>();
	protected String name;
	protected IStrategy strategy = new NullStrategy();
	
	public Component(){
		
	}
	
	public void addMediator(Mediator mediator){
		mediators.add(mediator);
	}
	
	public void rmMediator(Mediator mediator){
		mediators.remove(mediator);
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

}
