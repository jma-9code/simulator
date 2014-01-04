package model.mediator;

import model.component.IInput;
import model.component.IInputOutput;
import model.component.IOutput;
import model.response.IResponse;
import simulator.Context;

/**
 * Permet d'envoyer un message dans un canal multi-directionnel entre deux
 * composants Rq : Utilisable uniquement entre deux composants IO
 * 
 * @author JM
 * 
 */
public class HalfDuplexMediator extends Mediator {

	public HalfDuplexMediator() {

	}

	public HalfDuplexMediator(IInputOutput a, IInputOutput b) {
		super(a, b);

		// auto register
		Context.getInstance().registerMediator(this, true);
	}

	@Override
	public IResponse send(IOutput c, String data) {
		if (c == this.sender) {
			return this.receiver.notifyMessage(this, data);
		}
		else {
			return ((IInput) this.sender).notifyMessage(this, data);
		}
	}

	@Override
	public String toString() {
		return "M[HalfDuplex - " + this.sender + " <--> " + this.receiver + "]";
	}

}
