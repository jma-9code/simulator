package simulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncSimulator extends Simulator {
	
	private static Logger log = LoggerFactory.getLogger(AsyncSimulator.class);
	
	private ExecutorService executor;
	private Future transaction;
	private SimulatorException exception;
	
	/**
	 * Use SimulatorFactory.getAsyncSimulator()
	 */
	AsyncSimulator() {}
	
	@Override
	public void start() throws SimulatorException {
		// resource availability check
		if(executor == null || executor.isShutdown()) {
			log.debug("Fixed thread pool instanciation");
			executor = Executors.newFixedThreadPool(1);
		}
		
		// thread coherence check
		if(transaction != null && !transaction.isDone() && !transaction.isCancelled()) {
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
					
					if(e.getCause() instanceof InterruptedException) {
						log.info("Simulation stopped.");
					}
					else {
						exception = e;
						log.error("Error occured during the simulation", e);
					}
					
				}
			}
		};
		
		// submit the task, consider the transaction begins now.
		transaction = executor.submit(task);
	}
	
	/**
	 * Allow to stop the simulation.
	 */
	public void stop() {
		if(transaction != null && !transaction.isDone()) {
			log.info("Attempt to stop simulation");
			transaction.cancel(true);
		}
	}
	
	/**
	 * Return the exception or null if no error occured.
	 * @return
	 */
	public SimulatorException getException() {
		return exception;
	}
	
	/**
	 * Debug purposes
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	void waitUntilEnd() throws InterruptedException, ExecutionException {
		transaction.get();
	}
	
	/**
	 * @throws SimulatorException
	 */
	private void realStart() throws SimulatorException {
		super.start();
	}
}
