package simulator;

import java.util.ArrayList;
import java.util.Collection;
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
import model.factory.MediatorFactory;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;
import model.mediator.ReverseHalfDuplexMediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.StartPoint.StartPointComparator;
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

	private Map<String, List<IOutput>> events;

	public Context() {
		this.startPoints = new PriorityQueue<>(1, new StartPointComparator());
		this.components = new HashMap<>();
		this.mediators = new LinkedList<>();
		this.events = new HashMap<>();
	}

	/**
	 * Subscription method to an event.
	 * 
	 * @param component
	 * @param event
	 */
	public void subscribeEvent(IOutput component, String event) {
		List<IOutput> componentList = events.get(event);

		// Already exists ?
		if (componentList == null) {
			// no, so instanciate it.
			componentList = new LinkedList<>();
			events.put(event, componentList);
		}

		// add the component to be notify
		componentList.add(component);
	}

	/**
	 * Notifying all components subscribed to the event given.
	 * 
	 * @param event
	 */
	void notifyComponents(String event) {
		List<IOutput> componentList = events.get(event);

		if (componentList != null) {
			// iterate on component subscribed
			for (IOutput component : componentList) {
				// notify the component
				component.notifyEvent(event);
			}
		}
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
		if (autoRegistrationMode || !selfRegistration) {
			log.info("Register component " + component.getInstanceName());
			component.instanciate();
			components.put(component.getInstanceName(), component);
			log.debug(components.size() + " components");
		}
	}

	/**
	 * @see Context#registerComponent(Component, boolean)
	 */
	public void registerComponent(Component component) {
		registerComponent(component, false);
	}

	public void unregisterComponent(Component component) {
		log.info("Unregister component " + component.getInstanceName());
		components.remove(component);
	}

	/**
	 * Called by mediator factory to register all mediators in the context.
	 * 
	 * @param mediator
	 * @param selfRegistration
	 */
	public void registerMediator(Mediator mediator, boolean selfRegistration) {
		// auto-register
		if (autoRegistrationMode || !selfRegistration) {
			log.info("Register mediator " + mediator.toString());
			mediators.add(mediator);
			log.debug(mediators.size() + " mediators");
		}
	}

	/**
	 * Called by mediator factory to register all mediators in the context.
	 * 
	 * @param mediator
	 */
	public void registerMediator(Mediator mediator) {
		registerMediator(mediator, false);
	}

	public void unregisterMediator(Mediator mediator) {
		log.info("Unregister mediator " + mediator.toString());
		mediators.remove(mediator);
	}

	/**
	 * Return all components registred
	 * 
	 * @return
	 */
	Collection<Component> getAllComponents() {
		return components.values();
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
		List<Mediator> matches = getMediators(whoAreYou, whoWantYou);
		return matches != null && !matches.isEmpty() ? matches.get(0) : null;
	}

	/**
	 * Returns the mediator between the caller component and the component with
	 * the name given and which respect the key/value constraint given.
	 * 
	 * @param whoAreYou
	 *            Reference of caller component
	 * @param whoWantYou
	 *            Name of wanted component
	 * @param key
	 *            Key of property to check
	 * @param value
	 *            Value needed
	 * @return Reference of mediator to use.
	 * @throws ContextException
	 */
	public Mediator getFirstMediator(IOutput whoAreYou, String whoWantYou, String key, String value)
			throws ContextException {
		List<Mediator> mediators = getMediators(whoAreYou, whoWantYou);

		for (Mediator m : mediators) {
			if (m.getProperties() != null && m.getProperties().containsKey(key)) {
				// getting mediator value to compare with value needed
				String mValue = m.getProperties().get(key);
				log.debug("mValue = " + mValue);
				if (mValue != null && mValue.equals(value)) {
					return m;
				}
			}
		}

		log.warn("No mediator found to " + whoWantYou + " with " + key + "=" + value);

		return null;
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

		Iterator<Mediator> iMediators = mediators.iterator();
		List<Mediator> dstFiltered = new LinkedList<Mediator>();

		// filter mediator with destination
		while (iMediators.hasNext()) {
			Mediator curMediator = iMediators.next();

			if (whoWantYou.equalsIgnoreCase(curMediator.getReceiver().getName())
					|| whoWantYou.equalsIgnoreCase(curMediator.getSender().getName())) {
				dstFiltered.add(curMediator);
				log.debug("Mediator linking " + whoWantYou + " found : " + curMediator);
			}
		}

		// try to find mediator with depth = 1
		iMediators = dstFiltered.iterator();
		while (iMediators.hasNext()) {
			Mediator curMediator = iMediators.next();

			// half or simplex with whoAreYou in sender
			// or halfduplex with whoAreYou in receiver (reversable)
			if (curMediator.getSender() == whoAreYou
					|| (curMediator.getReceiver() == whoAreYou && curMediator instanceof HalfDuplexMediator)) {
				log.debug("Direct mediator found : " + curMediator);
				matches.add(curMediator);
			}
		}

		// filter mediator with source
		List<Mediator> srcFiltered = new LinkedList<Mediator>();
		iMediators = mediators.iterator();
		while (iMediators.hasNext()) {
			Mediator curMediator = iMediators.next();

			if (whoAreYou == curMediator.getSender() || whoAreYou == curMediator.getReceiver()) {
				srcFiltered.add(curMediator);
				log.debug("Mediator linked to " + whoAreYou.getName() + " found : " + curMediator);
			}
		}

		// not found, now try to find mediator with depth = 2
		iMediators = dstFiltered.iterator();
		while (iMediators.hasNext()) {
			Mediator m1 = iMediators.next();

			Iterator<Mediator> ite2 = srcFiltered.iterator();
			while (ite2.hasNext()) {
				Mediator m2 = ite2.next();

				log.debug("Is PipedMediator possible ? " + m1 + ", " + m2);

				// cas simplex vers simplex
				if (m1.getSender() == m2.getReceiver()) {
					// m1 link the component to another
					// m2 link this another to the component searched
					log.info("PipedMediator(m1, m2)");
					matches.add(MediatorFactory.getInstance().getPipedMediator(m1, m2));
				}
				// cas duplex vers simplex/duplex
				/*
				 * else if (m1 instanceof HalfDuplexMediator && m1.getSender()
				 * == m2.getSender()) { // m1 link another to the component //
				 * m2 link the component searched to this another
				 * log.info("PipedMediator(ReverseHalfDuplexMediator(m1), m2)");
				 * return MediatorFactory.getInstance().getPipedMediator( new
				 * ReverseHalfDuplexMediator((HalfDuplexMediator) m1), m2); }
				 */
				// cas duplex vers simplex/duplex
				else if (m2 instanceof HalfDuplexMediator && m1.getSender() == m2.getSender()) {
					// m1 link another to the component
					// m2 link the component searched to this another
					log.info("PipedMediator(ReverseHalfDuplexMediator(m2), m1)");
					matches.add(MediatorFactory.getInstance().getPipedMediator(
							new ReverseHalfDuplexMediator((HalfDuplexMediator) m2), m1));
				}
				// cas duplex vers duplex
				/*
				 * else if (m1 instanceof HalfDuplexMediator && m1.getReceiver()
				 * == m2.getReceiver()) { // m1 link another to the component //
				 * m2 link the component searched to this another
				 * log.info("PipedMediator(ReverseHalfDuplexMediator(m1), m2)");
				 * return MediatorFactory.getInstance().getPipedMediator( new
				 * ReverseHalfDuplexMediator((HalfDuplexMediator) m1), m2); }
				 */
				// cas duplex vers duplex
				else if (m2 instanceof HalfDuplexMediator && m1.getReceiver() == m2.getReceiver()) {
					// m1 link another to the component
					// m2 link the component searched to this another
					log.info("PipedMediator(ReverseHalfDuplexMediator(m2), m1)");
					matches.add(MediatorFactory.getInstance().getPipedMediator(
							new ReverseHalfDuplexMediator((HalfDuplexMediator) m2), m1));
				}
			}
		}

		if (matches.isEmpty()) {
			throw new ContextException("No mediator between component named " + whoWantYou + " and "
					+ whoAreYou.getName() + " in the context.");
		}

		return matches;
	}

	/**
	 * Allow to add a start point for the simulation<br />
	 * Note : invokable by UI or Component strategy
	 */
	public void addStartPoint(Date time, String event) {
		log.debug("Start point with event " + event + " and scheduled on " + time);
		StartPoint sp = new StartPoint(time, event);
		this.startPoints.add(sp);
	}

	public Queue<StartPoint> getStartPoints() {
		return startPoints;
	}

	/**
	 * Mecanism of registration components automatically at their instanciation.
	 */
	public void autoRegistrationMode() {
		autoRegistrationMode = true;
	}

	/**
	 * Reset the context entirely.
	 */
	public void reset() {
		startPoints.clear();
		current = null;
		currentCounter = 0;
		components.clear();
		mediators.clear();
		events.clear();
		autoRegistrationMode = false;
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
		return this.current != null ? this.current.time : null;
	}

	String getEvent() {
		return this.current != null ? this.current.event : null;
	}

	// Initialization on demand holder
	private static class ContextHolder {
		public static final Context instance = new Context();
	}

	public static Context getInstance() {
		return ContextHolder.instance;
	}

	public List<Component> getComponents() {
		return new ArrayList<Component>(components.values());
	}

	public void setComponents(Map<String, Component> components) {
		this.components = components;
	}

	public List<Mediator> getMediators() {
		return mediators;
	}

	public void setMediators(List<Mediator> mediators) {
		this.mediators = mediators;
	}

	public Map<String, List<IOutput>> getEvents() {
		return events;
	}

	public void setEvents(Map<String, List<IOutput>> events) {
		this.events = events;
	}

}
