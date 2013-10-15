package simulator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import model.component.IOutput;

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
		this.startPoints = new PriorityQueue<>(1, new StartPointComparator());
	}

	/**
	 * Allow to add a start point for the simulation Note : invokable by UI or
	 * Component strategy
	 */
	public void addStartPoint(Date time, IOutput component, String event) {
		log.debug("Start point added on " + component + " with event " + event + " and scheduled on " + time);
		StartPoint sp = new StartPoint(time, component, event);
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

	public IOutput getComponent() {
		return this.current.component;
	}

	public String getEvent() {
		return this.current.event;
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
		protected IOutput component;
		protected String event;

		public StartPoint(Date time, IOutput component, String event) {
			super();
			this.time = time != null ? time : Calendar.getInstance().getTime();
			this.component = component;
			this.event = event;
		}

	}

	public final class StartPointComparator implements Comparator<StartPoint> {

		@Override
		public int compare(StartPoint o1, StartPoint o2) {
			return o1.time != null ? o1.time.compareTo(o2.time) : -1;
		}

	}

}
