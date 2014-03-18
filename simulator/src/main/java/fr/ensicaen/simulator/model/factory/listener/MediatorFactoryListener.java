package fr.ensicaen.simulator.model.factory.listener;

import fr.ensicaen.simulator.model.mediator.Mediator;

public interface MediatorFactoryListener {
	public void addMediator(Mediator m);

	public void removeMediator(Mediator m);
}
