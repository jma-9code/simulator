package model.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Simulator;
import model.mediator.Mediator;

public class NullStrategy implements IStrategy {

	private static Logger log = LoggerFactory.getLogger(NullStrategy.class);
	
	@Override
	public void process(Mediator m, String data) {
		log.info("Input treatment with data = "+data);
	}

}
