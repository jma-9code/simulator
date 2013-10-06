package model.factory;

import java.util.Hashtable;

import model.component.Component;
import model.component.ComponentIO;
import model.component.IInput;
import model.component.IOutput;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;
import model.mediator.SimplexMediator;

public class MediatorFactory {

	public enum EMediator{
		SIMPLEX, HALFDUPLEX;
	}
	
	private Hashtable<String, Mediator> mediators;
	
	private MediatorFactory() {
		mediators = new Hashtable<String, Mediator>();
	}
	
	public Mediator getMediator(Component src, Component dst, EMediator channel){
		String uid = src.hashCode() + " " + dst.hashCode() + channel;
		if (mediators.containsKey(uid)){
			return mediators.get(uid);
		}
        else {
        	Mediator mediator = null;
        	switch(channel){
	        	case HALFDUPLEX:
	        		//verification des droits
	            	if (src instanceof ComponentIO && dst instanceof ComponentIO){
	            		mediator = new HalfDuplexMediator((ComponentIO)src, (ComponentIO)dst);
	            	}
	        		break;
	        	case SIMPLEX:
	        		if (src instanceof IOutput && dst instanceof IInput){
	            		mediator = new SimplexMediator((IOutput)src, (IInput)dst);
	            	}
	        		break;
        	}
            return mediator;
        }
	}

	private static class MediatorFactoryHolder
	{		
		/** Instance unique non préinitialisée */
		private final static MediatorFactory instance = new MediatorFactory();
	}
	
	/** Point d'accès pour l'instance unique du singleton */
	public static MediatorFactory getInstance()
	{
		return MediatorFactoryHolder.instance;
	}

}
