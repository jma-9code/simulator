package fr.ensicaen.simulator.simulator.listener;

import fr.ensicaen.simulator.model.mediator.Mediator;

public interface SimulatorListener {
	public void simulationStarted();

	public void simulationEnded();
}
