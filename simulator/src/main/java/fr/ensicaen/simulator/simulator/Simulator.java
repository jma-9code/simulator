package fr.ensicaen.simulator.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator.simulator.listener.SimulatorListener;

public class Simulator {

	private static Logger log = LoggerFactory.getLogger(Simulator.class);

	// allow to pause the simulation
	public static CyclicBarrier barrier = new CyclicBarrier(2);

	// list of all listeners
	private List<SimulatorListener> listeners = new ArrayList<SimulatorListener>();

	/**
	 * Use SimulatorFactory.getSimulator()
	 */
	Simulator() {
	}

	public void start() throws SimulatorException {
		Context ctx = Context.getInstance();

		// clear old events subscription and other things...
		if (ctx.getEvents() != null) {
			ctx.simulationReset();
		}

		// notify the context
		ctx.simulationStarted();

		// notify all listener
		for (SimulatorListener sl : listeners)
			sl.simulationStarted();

		// check if a start point is set
		if (!ctx.hasNext()) {
			log.error("No start point configured by user.");
			// throw new
			// SimulatorException("No start point configured by user.");
		}
		else {

			// init all output components
			for (Component c : Component.organizeComponents(ctx.getAllComponents())) {
				if (c.isOutput()) {
					((IOutput) c).init(ctx);
				}
			}

			while (ctx.hasNext()) {
				// change context
				ctx.next();
				log.info("Context just moved to the next start point, the date is " + ctx.getTime());

				// run simulation from start point defined
				log.info("Simulation context " + ctx.currentCounter() + " with event " + ctx.getEvent()
						+ " will start soon.");

				try {
					ctx.notifyComponents(ctx.getEvent());
				}
				catch (Throwable e) {
					log.error("Error occured during simulation, throw an exception", e);

					end();

					throw new SimulatorException(e);
				}

			}

		}
		end();
	}

	public void end() {
		// notify the context
		Context.getInstance().simulationEnded();

		// notify all listeners
		for (SimulatorListener sl : listeners)
			sl.simulationEnded();

		// remove all generate mediators during the simulation
		MediatorFactory.getInstance().removeAllDynamic();

		log.info("Simulation context " + Context.getInstance().currentCounter() + " ended");
	}

	/**
	 * Iterate to the next step of the simulation DO use this function, you need
	 * to call PAUSE
	 */
	public static void iterateStep() {
		if (barrier.getParties() != 2) {
			barrier = new CyclicBarrier(2);
		}

		try {
			barrier.await();
		}
		catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Possibility to iterate step by step
	 */
	public static void pausable() {
		if (barrier.getParties() != 2) {
			barrier = new CyclicBarrier(2);
		}
	}

	/**
	 * Skip step by step to do the full simulation
	 */
	public static void resume() {
		if (barrier.getParties() != 1) {
			barrier.reset();
			barrier = new CyclicBarrier(1);
		}

	}

	/**
	 * Add new listener in the simulator
	 * 
	 * @param toAdd
	 */
	public void addListener(SimulatorListener toAdd) {
		listeners.add(toAdd);
	}

}
