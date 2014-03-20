package fr.ensicaen.simulator.simulator.listener;


public interface SimulatorListener {
	public void simulationStarted();

	public void simulationEnded();

	public void startPointStarted();

	public void startPointEnded();
}
