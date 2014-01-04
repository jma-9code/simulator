package gui.bridge;

import java.io.Serializable;

import model.mediator.Mediator;

public class MediatorWrapper implements Serializable {

	private Mediator mediator;

	public MediatorWrapper(Mediator obj) {
		this.mediator = obj;
	}

	@Override
	public String toString() {
		return "";
	}

	public Mediator getMediator() {
		return mediator;
	}
}
