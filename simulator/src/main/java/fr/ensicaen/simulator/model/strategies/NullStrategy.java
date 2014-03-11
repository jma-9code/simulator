package fr.ensicaen.simulator.model.strategies;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.simulator.Context;

public class NullStrategy implements IStrategy<Component> {

	private static final long serialVersionUID = -8566363354337168824L;

	public NullStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Logger log = LoggerFactory.getLogger(NullStrategy.class);
	private String test = "toto";

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(Component component, Mediator mediator, String data) {
		log.info("Input treatment with data = " + data);
		return VoidResponse.build();
	}

	@Override
	public void processEvent(Component _this, String event) {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((test == null) ? 0 : test.hashCode());
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
		NullStrategy other = (NullStrategy) obj;
		if (test == null) {
			if (other.test != null)
				return false;
		}
		else if (!test.equals(other.test))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Null strategy";
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

}
