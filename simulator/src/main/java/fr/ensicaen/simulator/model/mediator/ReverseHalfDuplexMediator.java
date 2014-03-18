package fr.ensicaen.simulator.model.mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInputOutput;

public class ReverseHalfDuplexMediator extends HalfDuplexMediator {

	private static Logger log = LoggerFactory.getLogger(ReverseHalfDuplexMediator.class);

	public ReverseHalfDuplexMediator() {
	}

	public ReverseHalfDuplexMediator(HalfDuplexMediator mediator) {
		super((IInputOutput) mediator.getReceiver(), (IInputOutput) mediator.getSender());
	}
}
