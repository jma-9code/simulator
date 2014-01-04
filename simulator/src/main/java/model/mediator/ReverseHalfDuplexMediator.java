package model.mediator;

import model.component.IInput;
import model.component.IOutput;
import model.response.IResponse;

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
