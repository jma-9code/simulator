package fr.ensicaen.simulator.model.factory;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IInputOutput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.ChildHalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.ChildSimplexMediator;
import fr.ensicaen.simulator.model.mediator.ForwardMediator;
import fr.ensicaen.simulator.model.mediator.HalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.MediatorException;
import fr.ensicaen.simulator.model.mediator.PipedMediator;
import fr.ensicaen.simulator.model.mediator.SimplexMediator;

public class MediatorFactory {

	private static Logger log = LoggerFactory.getLogger(MediatorFactory.class);

	public enum EMediator {
		SIMPLEX, HALFDUPLEX, HALFDUPLEX_CHILD, SIMPLEX_CHILD;
	}

	private Hashtable<String, Mediator> mediators;

	private MediatorFactory() {
		this.mediators = new Hashtable<String, Mediator>();
	}

	public Mediator getMediator(Component src, Component dst, EMediator channel) {
		String uid = src.hashCode() + " " + dst.hashCode() + channel;

		if (this.mediators.containsKey(uid)) {
			return this.mediators.get(uid);
		}
		else {
			Mediator mediator = null;
			switch (channel) {
				case HALFDUPLEX:
					// verification des droits
					if (src instanceof IInputOutput && dst instanceof IInputOutput) {
						mediator = new HalfDuplexMediator((IInputOutput) src, (IInputOutput) dst);
					}
					break;
				case HALFDUPLEX_CHILD:
					if (src instanceof IInputOutput && dst instanceof IInputOutput) {
						mediator = new ChildHalfDuplexMediator((IInputOutput) src, (IInputOutput) dst);
					}
					break;
				case SIMPLEX:
					if (src instanceof IOutput && dst instanceof IInput) {
						mediator = new SimplexMediator((IOutput) src, (IInput) dst);
					}
					break;
				case SIMPLEX_CHILD:
					if (src instanceof IOutput && dst instanceof IInput) {
						mediator = new ChildSimplexMediator((IOutput) src, (IInput) dst);
					}
					break;
			}
			return mediator;
		}
	}

	public Mediator getMediator(Component src, Component dst) {
		boolean isChild = src.getChilds().contains(dst);

		if (src instanceof IInputOutput && dst instanceof IInputOutput) {
			return getMediator(src, dst, isChild ? EMediator.HALFDUPLEX_CHILD : EMediator.HALFDUPLEX);
		}
		else if (src instanceof IOutput && dst instanceof IInput) {
			return getMediator(src, dst, isChild ? EMediator.SIMPLEX_CHILD : EMediator.SIMPLEX);
		}
		else {
			return null;
		}
	}

	/**
	 * Permet de récupérer le médiateur de transfert.
	 * 
	 * @param origin
	 *            Médiateur d'origine
	 * @param dst
	 *            Destination
	 * @return
	 * @throws MediatorException
	 */
	public Mediator getForwardMediator(Mediator origin, IInput dst) {
		if (dst == null) {
			log.error("Destination is null !");
			return null;
		}

		return new ForwardMediator(origin, dst);
	}

	/**
	 * Permet de récupérer un médiateur agissant comme pipe entre deux
	 * médiateurs.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public Mediator getPipedMediator(Mediator m1, Mediator m2) {
		return new PipedMediator(m1, m2);
	}

	private static class MediatorFactoryHolder {
		/** Instance unique non préinitialisée */
		private final static MediatorFactory instance = new MediatorFactory();
	}

	/** Point d'accès pour l'instance unique du singleton */
	public static MediatorFactory getInstance() {
		return MediatorFactoryHolder.instance;
	}

}
