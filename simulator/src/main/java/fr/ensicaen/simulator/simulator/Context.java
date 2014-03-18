package fr.ensicaen.simulator.simulator;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.ForwardMediator;
import fr.ensicaen.simulator.model.mediator.HalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.PipedMediator;
import fr.ensicaen.simulator.model.mediator.ReverseHalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.SimplexMediator;
import fr.ensicaen.simulator.model.properties.listener.DefaultPropertyListenerImpl;
import fr.ensicaen.simulator.model.properties.listener.PropertyListener;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.StartPoint.StartPointComparator;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator.simulator.listener.SimulatorListener;

/**
 * Simulation context configured by user, controlled by the simulator and
 * accessible from components.
 * 
 * @author Flo
 */
public class Context implements SimulatorListener {

	private static Logger log = LoggerFactory.getLogger(Context.class);

	/**
	 * List of start points sorted by date defined by the user
	 */
	private Queue<StartPoint> userStartPoints;

	/**
	 * List of start points sorted by date for the current execution
	 */
	private Queue<StartPoint> execStartPoints;

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
	 * In this mode, all component instancied when true, will be automatically
	 * register in the context.
	 */
	private boolean autoRegistrationMode = false;

	/**
	 * Liste des composants enregistres pour un evènement key = Evènement value
	 * = Liste des composants
	 */
	private Map<String, List<IOutput>> events;

	/**
	 * Property listener à utiliser. Ce decouplage permet à la couche GUI de
	 * definir un listener particulier (ex : popup de renseignement de
	 * l'information necessaire).
	 */
	private PropertyListener propertyListener = new DefaultPropertyListenerImpl();

	/**
	 * Simulation in execution ?
	 */
	private boolean inExecution = false;

	public Context() {
		this.userStartPoints = new PriorityQueue<>(1, new StartPointComparator());
		this.components = new HashMap<>();
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

	/**
	 * @param component
	 */
	public void unregisterComponent(Component component) {
		log.info("Unregister component " + component.getInstanceName());
		components.remove(component);
	}

	/**
	 * Retourne la liste des composants du context ayant le type specifie
	 * 
	 * @param type
	 * @return
	 */
	public List<Component> getComponentsType(int type) {
		List<Component> comps = new ArrayList<>();
		for (Component c : components.values()) {
			if (c.getType() == type) {
				comps.add(c);
			}
		}
		return comps;
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
			MediatorFactory.getInstance().add(mediator);
			log.debug(getMediators().size() + " mediators");
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
		MediatorFactory.getInstance().remove(mediator);
		// mediators.remove(mediator);
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
	 *            Type of wanted component
	 * @return Reference of mediator to use.
	 */
	public Mediator getFirstMediator(IOutput whoAreYou, int whoWantYou) throws ContextException {
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
	 *            type of wanted component
	 * @param key
	 *            Key of property to check
	 * @param value
	 *            Value needed
	 * @return Reference of mediator to use.
	 * @throws ContextException
	 */
	public Mediator getFirstMediator(IOutput whoAreYou, int whoWantYou, String key, String value)
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

		log.warn("No mediator found to type " + whoWantYou + "  with " + key + "=" + value);

		return null;
	}

	/**
	 * Get all mediators link with the component
	 * 
	 * @param c
	 * @return
	 */
	private List<Mediator> getAllLinkedMediators(Component c) {
		List<Mediator> ret = new ArrayList<>();
		Iterator<Mediator> iMediators = MediatorFactory.getInstance().getMediators().iterator();
		while (iMediators.hasNext()) {
			Mediator cur = iMediators.next();
			if (cur instanceof ForwardMediator || cur instanceof ReverseHalfDuplexMediator
					|| cur instanceof PipedMediator) {
				continue;
			}
			if (cur.getSender().equals(c) || cur.getReceiver().equals(c)) {
				ret.add(cur);
			}
		}
		return ret;
	}

	/**
	 * retrive the full mediators path for link src and dst (rec)
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	private List<Mediator> getMediatorPath(Component src, final Component dst, List<Mediator> explored) {
		List<Mediator> ret = new LinkedList<>();
		List<Mediator> src_meds = getAllLinkedMediators(src);
		// retire les liens explores
		src_meds.removeAll(explored);

		for (Mediator m : src_meds) {
			explored.add(m);
			// src (sender)<--->dst
			if (!m.getReceiver().equals(src)) {
				// ret.add(m);
				if (m.getReceiver().equals(dst)) {
					// find the dst component
					ret.add(m);
					return ret;
				}
				else {
					List<Mediator> tmp = getMediatorPath((Component) m.getReceiver(), dst, explored);
					if (!tmp.isEmpty()) {
						ret.add(m);
						ret.addAll(tmp);
					}
				}
			}
			// dst (sender)<---->src
			else {
				// sender = dst, il faut un half duplex
				if (m instanceof HalfDuplexMediator) {
					// ret.add(m);
					if (m.getSender().equals(dst)) {
						// find the dst component, invert the direction
						ret.add(new ReverseHalfDuplexMediator((HalfDuplexMediator) m));
						return ret;
					}
					else {
						List<Mediator> tmp = getMediatorPath((Component) m.getSender(), dst, explored);
						if (!tmp.isEmpty()) {
							// invert the direction
							ret.add(new ReverseHalfDuplexMediator((HalfDuplexMediator) m));
							ret.addAll(tmp);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Returns mediators between the caller component and components with the
	 * type given.
	 * 
	 * @param whoAreYou
	 *            Reference of caller component
	 * @param typeYouWant
	 *            type of wanted component
	 * @return List of mediators with potential multiple components
	 */
	public List<Mediator> getMediators(IOutput whoAreYou, int typeYouWant) throws ContextException {
		// candidats potentiels
		List<Mediator> matches = new LinkedList<>();
		// tout les composants du context
		List<Component> comps = new ArrayList<Component>(components.values());
		Component whoWantYou_c = null;

		// recuperation des mediators associes au whoareYou
		List<Mediator> whoAreYou_meds = getAllLinkedMediators((Component) whoAreYou);

		// recuperation du composant root de whoAreYou
		Component root = Component.getRoot((Component) whoAreYou);
		// recuperation des mediators associes au whoareYou de plus haut niveau
		List<Mediator> root_meds = getAllLinkedMediators(root);

		// recherche d'un lien direct entre le receiver et sender
		for (Mediator m : whoAreYou_meds) {
			// le receiver est du type recherche
			if (m.getReceiver().getType() == typeYouWant
					&& (m instanceof SimplexMediator || m instanceof HalfDuplexMediator)) {
				matches.add(m);
			}
			// le sender est du type recherche, il faut etre en halfduplex
			else if (m.getSender().getType() == typeYouWant && m instanceof HalfDuplexMediator) {
				matches.add(m);
			}
		}

		// recherche d'un lien indirect entre le receiver et sender (utilisation
		// du composant root du whoareyou)
		Component tmp = null;
		for (Mediator m : root_meds) {
			if (m.getReceiver().equals(root)) {
				tmp = (Component) m.getSender();
				// recuperation du composant root de tmp
				Component root_tmp = Component.getRoot(tmp);

				tmp = Component.containsType(root_tmp, typeYouWant);
				// le sender est du type recherche, il faut etre en halfduplex
				if (tmp != null && m instanceof HalfDuplexMediator) {
					List<Mediator> media = getMediatorPath((Component) whoAreYou, tmp, new LinkedList<Mediator>());

					Iterator<Mediator> it = media.iterator();
					Mediator buff = null;
					while (it.hasNext()) {
						Mediator me = it.next();
						if (buff == null) {
							buff = me;
						}
						else {
							buff = MediatorFactory.getInstance().getPipedMediator(buff, me);
						}
					}
					matches.add(buff);

					// matches.add(MediatorFactory.getInstance().getMediator((Component)
					// whoAreYou, tmp,
					// EMediator.HALFDUPLEX));
				}
			}
			else {
				tmp = (Component) m.getReceiver();
				// recuperation du composant root de tmp
				Component root_tmp = Component.getRoot(tmp);

				tmp = Component.containsType(root_tmp, typeYouWant);
				// le receiver est du type recherche
				if (tmp != null && (m instanceof SimplexMediator || m instanceof HalfDuplexMediator)) {
					List<Mediator> media = getMediatorPath((Component) whoAreYou, tmp, new LinkedList<Mediator>());

					Iterator<Mediator> it = media.iterator();
					Mediator buff = null;
					while (it.hasNext()) {
						Mediator me = it.next();
						if (buff == null) {
							buff = me;
						}
						else {
							buff = MediatorFactory.getInstance().getPipedMediator(buff, me);
						}
					}
					matches.add(buff);
					// matches.add(MediatorFactory.getInstance().getMediator((Component)
					// whoAreYou, tmp,
					// (m instanceof SimplexMediator) ? EMediator.SIMPLEX :
					// EMediator.HALFDUPLEX));
				}
			}
		}

		if (matches.isEmpty()) {
			throw new ContextException("No mediator between component type " + typeYouWant + " and "
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

		if (inExecution) {
			this.execStartPoints.add(sp);
		}
		else {
			this.userStartPoints.add(sp);
		}
	}

	public Queue<StartPoint> getUserStartPoints() {
		return userStartPoints;
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
		simulationReset();
		userStartPoints.clear();
		components.clear();
		MediatorFactory.getInstance().reset();
		currentCounter = 0;
		autoRegistrationMode = false;
	}

	public void simulationReset() {
		events.clear();
		current = null;
		execStartPoints = null;
	}

	/**
	 * Restore the context from object scenarioData (persistance of context)
	 * 
	 * @param sd
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void restoreContext(ScenarioData sd) throws InstantiationException, IllegalAccessException {
		reset();
		userStartPoints.addAll(sd.getStartPoints());
		components.putAll(sd.getRootComponents());
		MediatorFactory.getInstance().addAll(sd.getMediators());
		events.putAll(sd.getEvents());

		// on all components (root and childs)
		for (Component c : Component.organizeComponents(components.values())) {

			// associate strategy and component
			Class strat = sd.getLink_strat_component().get(c.getUuid());
			if (strat != null) {
				c.setStrategy((IStrategy<? extends Component>) strat.newInstance());
			}
		}
	}

	/**
	 * Another start point available ? Note : invoke by simulator "only"
	 * 
	 * @return true or false
	 */
	boolean hasNext() {
		return this.execStartPoints.peek() != null;
	}

	/**
	 * Go on next start point Note : invoke by simulator "only"
	 */
	void next() {
		// stop auto registration of main components and mediators associated
		if (autoRegistrationMode) {
			autoRegistrationMode = false;
		}

		this.current = this.execStartPoints.poll();
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

	public Map<String, Component> getComponents() {
		return components;
	}

	public void setComponents(Map<String, Component> components) {
		this.components = components;
	}

	public Collection<Mediator> getMediators() {
		return MediatorFactory.getInstance().getMediators();
	}

	public Collection<Mediator> getExplicitMediators() {
		return MediatorFactory.getInstance().getExplicitMediators();
	}

	public Map<String, List<IOutput>> getEvents() {
		return events;
	}

	public void setEvents(Map<String, List<IOutput>> events) {
		this.events = events;
	}

	public PropertyListener getPropertyListener() {
		return propertyListener;
	}

	public void setPropertyListener(PropertyListener propertyListener) {
		this.propertyListener = propertyListener;
	}

	@Override
	public void simulationStarted() {
		// initialisation de la queue d'execution qui sera depilee à chaque
		// iteration de point de demarrage
		execStartPoints = new PriorityQueue<>(userStartPoints.size(), new StartPointComparator());
		execStartPoints.addAll(userStartPoints);
		inExecution = true;
	}

	@Override
	public void simulationEnded() {
		inExecution = false;
	}

}
