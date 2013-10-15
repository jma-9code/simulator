package simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulator {

	private static Logger log = LoggerFactory.getLogger(Simulator.class);

	/**
	 * Use SimulatorFactory.getSimulator()
	 */
	Simulator() {
	}

	public void start() throws SimulatorException {

		Context ctx = Context.getInstance();
		if (!ctx.hasNext()) {
			throw new SimulatorException("No start point configured by user.");
		}

		while (ctx.hasNext()) {
			// change context
			ctx.next();
			log.info("Context just moved to the next start point, the date is " + ctx.getTime());

			// run simulation from start point defined
			log.info("Simulation context " + ctx.currentCounter() + " from " + ctx.getComponent() + " with event "
					+ ctx.getEvent() + " will start soon.");

			try {
				ctx.getComponent().notifyEvent(ctx.getEvent());
			}
			catch (Throwable e) {
				log.error("Error occured during simulation, throw an exception");
				throw new SimulatorException(e);
			}

			log.info("Simulation context " + ctx.currentCounter() + " ended");
		}

	}

}
