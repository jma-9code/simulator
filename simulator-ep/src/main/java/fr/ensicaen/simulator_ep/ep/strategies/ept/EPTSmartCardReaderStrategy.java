package fr.ensicaen.simulator_ep.ep.strategies.ept;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator_ep.utils.ComponentEP;

public class EPTSmartCardReaderStrategy implements IStrategy<ComponentIO> {
	private static Logger log = LoggerFactory.getLogger(EPTSmartCardReaderStrategy.class);

	public EPTSmartCardReaderStrategy() {
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
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		try {
			Mediator m = Context.getInstance().getFirstMediator(_this, ComponentEP.ETP_SMART_CARD_READER.ordinal());
			return m.send(_this, data);
		}
		catch (ContextException e) {
			log.error("Context error", e);
			return DataResponse.build(mediator, "ERROR"); // TODO Error code in
															// protocol ?
		}
	}

	@Override
	public String toString() {
		return "EPT/SmartCardReader";
	}
}
