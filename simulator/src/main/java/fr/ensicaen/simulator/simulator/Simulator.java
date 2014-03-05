package fr.ensicaen.simulator.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;

public class Simulator {

	private static Logger log = LoggerFactory.getLogger(Simulator.class);

	/**
	 * Use SimulatorFactory.getSimulator()
	 */
	Simulator() {
	}

	public void start() throws SimulatorException {

		Context ctx = Context.getInstance();

		// check if a start point is set
		if (!ctx.hasNext()) {
			throw new SimulatorException("No start point configured by user.");
		}

		// init all output components
		for (Component c : organizeComponents(ctx.getAllComponents())) {
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
				throw new SimulatorException(e);
			}

			log.info("Simulation context " + ctx.currentCounter() + " ended");
		}

	}

	/**
	 * Recursive function to re-organize components list.
	 * 
	 * @param components
	 * @return
	 */
	public static List<Component> organizeComponents(Collection<Component> components) {
		List<Component> ret = new ArrayList<>();
		ret.addAll(components);
		for (Component c : components) {
			List<Component> tmp = organizeComponents(c.getChilds());
			for (Component c1 : tmp) {
				if (!components.contains(c1)) {
					ret.add(c1);
				}
			}
		}
		return ret;
	}

}
