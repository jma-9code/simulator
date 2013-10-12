package model.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Simulator;
import model.component.Component;
import model.mediator.Mediator;
import model.response.IResponse;
import model.response.VoidResponse;

public class NullStrategy implements IStrategy<Component> {

	private static Logger log = LoggerFactory.getLogger(NullStrategy.class);
	
	@Override
	public IResponse processMessage(Component component, Mediator mediator, String data) {
		log.info("Input treatment with data = "+data);
		return VoidResponse.build();
	}

}
