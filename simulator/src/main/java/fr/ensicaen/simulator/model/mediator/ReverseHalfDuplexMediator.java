package fr.ensicaen.simulator.model.mediator;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.response.IResponse;

public class ReverseHalfDuplexMediator extends Mediator {

	public ReverseHalfDuplexMediator() {

	}

	public ReverseHalfDuplexMediator(HalfDuplexMediator mediator) {
		super((IOutput) mediator.getReceiver(), (IInput) mediator.getSender());
	}

	@Override
	public IResponse send(IOutput c, String data) {
		if (c == this.sender) {
			return this.receiver.notifyMessage(this, data);
		}
		else {
			return ((IInput) this.sender).notifyMessage(this, data);
		}
	}

	@Override
	public String toString() {
		return "M[ReverseHalfDuplex - " + this.sender + " <--> " + this.receiver + "]";
	}

}
