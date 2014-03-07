package fr.ensicaen.simulator.simulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.simulator.exception.SimulatorException;

public class AsyncSimulator extends Simulator {

	private static Logger log = LoggerFactory.getLogger(AsyncSimulator.class);

	private ExecutorService executor;
	private Future transaction;
	private SimulatorException exception;

	/**
	 * Use SimulatorFactory.getAsyncSimulator()
	 */
	AsyncSimulator() {
	}

	@Override
	public void start() throws SimulatorException {
		// resource availability check
		if (this.executor == null || this.executor.isShutdown()) {
			log.debug("Fixed thread pool instanciation");
			this.executor = Executors.newFixedThreadPool(1);
		}

		// thread coherence check
		if (this.transaction != null && !this.transaction.isDone()) {
			throw new SimulatorException("A simulation is already running.");
		}

		// construct the async task
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					AsyncSimulator.this.realStart();
				}
				catch (SimulatorException e) {

					// reinit
					transaction = null;

					if (e.getCause() instanceof InterruptedException) {
						log.info("Simulation stopped.");
					}
					else {
						AsyncSimulator.this.exception = e;
						log.error("Error occured during the simulation", e);
					}

				}
			}
		};

		// submit the task, consider the transaction begins now.
		this.transaction = this.executor.submit(task);
	}

	/**
	 * Allow to stop the simulation.
	 */
	public void stop() {
		if (this.transaction != null && !this.transaction.isDone()) {
			log.info("Attempt to stop simulation");
			this.transaction.cancel(true);
		}
	}

	/**
	 * Return the exception or null if no error occured.
	 * 
	 * @return
	 */
	public SimulatorException getException() {
		return this.exception;
	}

	/**
	 * Debug purposes
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	void waitUntilEnd() throws InterruptedException, ExecutionException {
		this.transaction.get();
	}

	/**
	 * @throws SimulatorException
	 */
	private void realStart() throws SimulatorException {
		super.start();
	}
}
