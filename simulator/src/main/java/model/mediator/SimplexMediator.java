package model.mediator;

import model.component.Component;
import model.component.IInput;
import model.component.IOutput;

/**
 * Permet d'envoyer un message dans un seul sens (unidirectionnel)
 * Rq : Utilisable uniquement si le composant emeteur a une sortie, et que le composant recepteur a une entree
 * @author JM
 *
 */
public class SimplexMediator extends Mediator {

	public SimplexMediator(IOutput _sender, IInput _receiver) {
		super(_sender, _receiver);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Envoie d'un msg de A vers B
	 * @param data
	 */
	public void send (Component c, String data){
		receiver.input(this, data);
	}
}
