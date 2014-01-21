package fr.ensicaen.simulator.simulator;

public class SimulatorFactory {

	private static AsyncSimulator asyncSimulator;
	private static Simulator simulator;

	public static AsyncSimulator getAsyncSimulator() {
		if (asyncSimulator == null) {
			asyncSimulator = new AsyncSimulator();
		}

		return asyncSimulator;
	}

	public static Simulator getSimulator() {
		if (simulator == null) {
			simulator = new Simulator();
		}

		return simulator;
	}

}
