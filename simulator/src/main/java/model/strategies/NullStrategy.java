package model.strategies;

import model.component.Component;
import model.component.IOutput;
import model.mediator.Mediator;
import model.response.IResponse;
import model.response.VoidResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;

public class NullStrategy implements IStrategy<Component> {

	private static Logger log = LoggerFactory.getLogger(NullStrategy.class);

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
	public String toString() {
		return "Null strategy";
	}

}
