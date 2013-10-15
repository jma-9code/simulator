package simulator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import model.component.IOutput;
import model.mediator.Mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulation context configured by user, controlled by the simulator and
 * accessible from components.
 * 
 * @author Flo
 */
public class Context {

	private static Logger log = LoggerFactory.getLogger(Context.class);

	/**
	 * List of start points sorted by date
	 */
	private Queue<StartPoint> startPoints;

	/**
	 * Delegate access Modified by simulator via next() method.
	 */
	private StartPoint current;

	/**
	 * Context counter
	 */
	private short currentCounter = 0;

	public Context() {
		this.startPoints = new PriorityQueue(1, new StartPointComparator());
	}

	/**
	 * Allow to add a start point for the simulation Note : invokable by UI or
	 * Component strategy
	 */
	public void addStartPoint(Date time, IOutput sender, Mediator mediator, String data) {
		log.debug("Start point added on " + sender + " via " + mediator + " and scheduled on " + time);
		StartPoint sp = new StartPoint(time, sender, mediator, data);
		this.startPoints.add(sp);
	}

	/**
	 * Another start point available ? Note : invoke by simulator "only"
	 * 
	 * @return true or false
	 */
	boolean hasNext() {
		return this.startPoints.peek() != null;
	}

	/**
	 * Go on next start point Note : invoke by simulator "only"
	 */
	void next() {
		this.current = this.startPoints.poll();
		this.currentCounter++;
	}

	/**
	 * Current context counter. Increment by 1 when next() method is invoke.
	 * 
	 * @return
	 */
	public short currentCounter() {
		return this.currentCounter;
	}

	public Date getTime() {
		return this.current.time;
	}

	public IOutput getSender() {
		return this.current.sender;
	}

	public Mediator getMediator() {
		return this.current.mediator;
	}

	public String getData() {
		return this.current.data;
	}

	// Initialization on demand holder
	private static class ContextHolder {
		public static final Context instance = new Context();
	}

	public static Context getInstance() {
		return ContextHolder.instance;
	}

	// POJO StartPoint
	public final static class StartPoint {

		protected Date time;
		protected IOutput sender;
		protected Mediator mediator;
		protected String data;

		public StartPoint(Date time, IOutput sender, Mediator mediator, String data) {
			super();
			this.time = time != null ? time : Calendar.getInstance().getTime();
			this.sender = sender;
			this.mediator = mediator;
			this.data = data;
		}

	}

	public final class StartPointComparator implements Comparator<StartPoint> {

		@Override
		public int compare(StartPoint o1, StartPoint o2) {
			return o1.time != null ? o1.time.compareTo(o2.time) : -1;
		}

	}

}
