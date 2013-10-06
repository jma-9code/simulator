package model.mediator;

import model.component.Component;
import model.component.ComponentIO;
import model.component.IInput;
import model.component.IOutput;

/**
 * Permet d'envoyer un message dans un canal multi-directionnel entre deux composants
 * Rq : Utilisable uniquement entre deux composants IO
 * @author JM
 *
 */
public class HalfDuplexMediator extends Mediator {

	public HalfDuplexMediator(ComponentIO a, ComponentIO b) {
		super(a, b);
		// TODO Auto-generated constructor stub
	}
	
	public void send (IOutput c, String data){
		if (c == sender){
			receiver.input(this, data);
		}
		else{
			((IInput) sender).input(this, data);
		}
	}

}
