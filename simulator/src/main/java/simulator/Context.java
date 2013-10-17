package simulator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import model.component.Component;
import model.component.IOutput;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.exception.ContextException;

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

	/**
	 * List of main components
	 */
	private Map<String, Component> components;

	/**
	 * List of static mediators
	 */
	private List<Mediator> mediators;

	/**
	 * In this mode, all component instancied when true, will be automatically
	 * register in the context.
	 */
	private boolean autoRegistrationMode = false;

	public Context() {
		this.startPoints = new PriorityQueue<>(1, new StartPointComparator());
		this.components = new HashMap<>();
		this.mediators = new LinkedList<>();
	}

	/**
	 * Called by component on object instanciation or the simulator user to
	 * register components in the context.
	 * 
	 * @param component
	 *            Component
	 * @param selfRegistration
	 */
	public void registerComponent(Component component, boolean selfRegistration) {
		// auto-register
		if (autoRegistrationMode) {
			components.put(component.getName(), component);
		}
	}

	/**
	 * @see Context#registerComponent(Component, boolean)
	 */
	public void registerComponent(Component component) {
		registerComponent(component, false);
	}

	/**
	 * Called by mediator factory to register all mediators in the context.
	 * 
	 * @param mediator
	 * @param selfRegistration
	 */
	public void registerMediator(Mediator mediator, boolean selfRegistration) {
		// auto-register
		if (autoRegistrationMode) {
			mediators.add(mediator);
		}
	}

	/**
	 * Called by mediator factory to register all mediators in the context.
	 * 
	 * @param mediator
	 */
	public void registerMediator(Mediator mediator) {
		mediators.add(mediator);
	}

	/**
	 * Returns the component which matches with the name passed.
	 * 
	 * @param name
	 *            Component's name
	 * @return Component or null.
	 */
	public Component getComponent(String name) throws ContextException {
		Component component = components.get(name);

		if (component == null) {
			throw new ContextException("No component named " + name + " in the context.");
		}

		return component;
	}

	/**
	 * Returns the mediator between the caller component and the component with
	 * the name given.
	 * 
	 * @param whoAreYou
	 *            Reference of caller component
	 * @param whoWantYou
	 *            Name of wanted component
	 * @return Reference of mediator to use.
	 */
	public Mediator getFirstMediator(IOutput whoAreYou, String whoWantYou) throws ContextException {
		Iterator<Mediator> ite = mediators.iterator();

		while (ite.hasNext()) {
			Mediator mediator = ite.next();

			// simplex case
			if (whoAreYou == mediator.getSender() && whoWantYou.equalsIgnoreCase(mediator.getReceiver().getName())) {

				return mediator;
			}

			// duplex case (inverse)
			if (mediator instanceof HalfDuplexMediator
					&& (whoAreYou == mediator.getReceiver() && whoWantYou.equalsIgnoreCase(mediator.getSender()
							.getName()))) {
				return mediator;
			}
		}

		throw new ContextException("No mediator between component named " + whoWantYou + " linked to "
				+ whoAreYou.getName() + " in the context.");
	}

	/**
	 * Returns mediators between the caller component and components with the
	 * name given.
	 * 
	 * @param whoAreYou
	 *            Reference of caller component
	 * @param whoWantYou
	 *            Name of wanted component
	 * @return List of mediators with potential multiple components
	 */
	public List<Mediator> getMediators(IOutput whoAreYou, String whoWantYou) throws ContextException {
		List<Mediator> matches = new LinkedList<>();
		Iterator<Mediator> ite = mediators.iterator();

		while (ite.hasNext()) {
			Mediator mediator = ite.next();

			// simplex case
			if (whoAreYou == mediator.getSender() && whoWantYou.equalsIgnoreCase(mediator.getReceiver().getName())) {
				matches.add(mediator);
			}

			// duplex case (inverse)
			if (mediator instanceof HalfDuplexMediator
					&& (whoAreYou == mediator.getReceiver() && whoWantYou.equalsIgnoreCase(mediator.getSender()
							.getName()))) {
				matches.add(mediator);
			}
		}

		if (matches.isEmpty()) {
			throw new ContextException("No mediator between component named " + whoWantYou + " linked to "
					+ whoAreYou.getName() + " in the context.");
		}

		return matches;
	}

	/**
	 * Allow to add a start point for the simulation<br />
	 * Note : invokable by UI or Component strategy
	 */
	public void addStartPoint(Date time, IOutput component, String event) {
		log.debug("Start point added on " + component + " with event " + event + " and scheduled on " + time);
		StartPoint sp = new StartPoint(time, component, event);
		this.startPoints.add(sp);
	}

	/**
	 * Mecanism of registration components automatically at their instanciation.
	 */
	public void autoRegistrationMode() {
		autoRegistrationMode = true;
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
		// stop auto registration of main components and mediators associated
		if (autoRegistrationMode) {
			autoRegistrationMode = false;
		}

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

	IOutput getComponent() {
		return this.current.component;
	}

	String getEvent() {
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
