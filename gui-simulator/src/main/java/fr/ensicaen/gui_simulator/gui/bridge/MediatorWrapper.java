package fr.ensicaen.gui_simulator.gui.bridge;

import java.io.Serializable;

import fr.ensicaen.simulator.model.mediator.Mediator;

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
