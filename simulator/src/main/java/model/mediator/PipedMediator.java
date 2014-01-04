package model.mediator;

import model.component.IInput;
import model.component.IOutput;
import model.response.IResponse;

public class PipedMediator extends Mediator {

	private Mediator m1;
	private Mediator m2;

	public PipedMediator() {

	}

	public PipedMediator(Mediator m1, Mediator m2) {
		super(m1.getSender(), m2.getReceiver());
		this.m1 = m1;
		this.m2 = m2;
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

	public Mediator getM1() {
		return m1;
	}

	public Mediator getM2() {
		return m2;
	}

}
