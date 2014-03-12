package fr.ensicaen.simulator.model.mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInputOutput;

public class ReverseHalfDuplexMediator extends HalfDuplexMediator {

	private static Logger log = LoggerFactory.getLogger(ReverseHalfDuplexMediator.class);

	public ReverseHalfDuplexMediator() {
		super(null, null);
	}

	public ReverseHalfDuplexMediator(HalfDuplexMediator mediator) {
		super((IInputOutput) mediator.getSender(), (IInputOutput) mediator.getReceiver());
	}

	@Override
	public String toString() {
		return "M[ReverseHalfDuplex - " + this.sender + " <--> " + this.receiver + "]";
	}

}
