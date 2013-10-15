package model.factory;

import java.util.Hashtable;

import model.component.Component;
import model.component.IInput;
import model.component.IInputOutput;
import model.component.IOutput;
import model.mediator.ForwardMediator;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;
import model.mediator.MediatorException;
import model.mediator.SimplexMediator;

public class MediatorFactory {

	public enum EMediator {
		SIMPLEX, HALFDUPLEX;
	}

	private Hashtable<String, Mediator> mediators;

	private MediatorFactory() {
		this.mediators = new Hashtable<String, Mediator>();
	}

	public Mediator getMediator(Component src, Component dst, EMediator channel) {
		String uid = src.hashCode() + " " + dst.hashCode() + channel;
		if (this.mediators.containsKey(uid)) {
			return this.mediators.get(uid);
		} else {
			Mediator mediator = null;
			switch (channel) {
			case HALFDUPLEX:
				// verification des droits
				if (src instanceof IInputOutput && dst instanceof IInputOutput) {
					mediator = new HalfDuplexMediator((IInputOutput) src, (IInputOutput) dst);
				}
				break;
			case SIMPLEX:
				if (src instanceof IOutput && dst instanceof IInput) {
					mediator = new SimplexMediator((IOutput) src, (IInput) dst);
				}
				break;
			}
			return mediator;
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
		return new ForwardMediator(origin, dst);
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
