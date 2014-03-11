package fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;

public class FOAcquirerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOAcquirerAuthorizationStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO FOAcquirerAuthorization, Mediator m, String data) {

		try {
			Mediator mediateurAUtiliser = Context.getInstance().getFirstMediator(FOAcquirerAuthorization, "Router");
			return mediateurAUtiliser.send(FOAcquirerAuthorization, data);
		}
		catch (ContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FO/Acquirer/Authorization";
	}

}
