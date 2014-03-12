package fr.ensicaen.simulator.model.mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.simulator.Context;

/**
 * Permet d'envoyer un message dans un seul sens (unidirectionnel) Rq :
 * Utilisable uniquement si le composant emeteur a une sortie, et que le
 * composant recepteur a une entree
 * 
 * @author JM
 * 
 */
public class SimplexMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(SimplexMediator.class);

	public SimplexMediator() {

	}

	public SimplexMediator(IOutput _sender, IInput _receiver) {
		super(_sender, _receiver);

		// auto register
		Context.getInstance().registerMediator(this, true);
	}

	/**
	 * Envoie d'un msg de A vers B
	 * 
	 * @param data
	 */
	@Override
	public VoidResponse send(IOutput c, String data) {
		IResponse response = this.receiver.notifyMessage(this, data);
		if (response == null || !(response instanceof VoidResponse)) {
			log.error("Invalid simplex response : must return VoidResponse !");
		}

		return (VoidResponse) response;
	}

	@Override
	public String toString() {
		return "M[Simplex - " + this.sender + " --> " + this.receiver + "]";
	}
}
