package model.mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.component.Component;
import model.component.IInput;
import model.component.IOutput;
import model.response.IResponse;
import model.response.VoidResponse;

/**
 * Permet d'envoyer un message dans un seul sens (unidirectionnel)
 * Rq : Utilisable uniquement si le composant emeteur a une sortie, et que le composant recepteur a une entree
 * @author JM
 *
 */
public class SimplexMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(SimplexMediator.class);
	
	public SimplexMediator(IOutput _sender, IInput _receiver) {
		super(_sender, _receiver);
	}
	
	/**
	 * Envoie d'un msg de A vers B
	 * @param data
	 */
	@Override
	public VoidResponse send (IOutput c, String data){
		IResponse response = receiver.input(this, data);
		if(response == null || !(response instanceof VoidResponse)) {
			log.error("Invalid simplex response : must return VoidResponse !");
		}
		
		return (VoidResponse) response;
	}
	
	@Override
	public String toString() {
		return "M[Simplex - "+sender+" --> "+receiver+"]";
	}
}
