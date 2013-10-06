package model.mediator;

import java.util.Arrays;
import java.util.List;

import model.component.Component;
import model.component.ComponentIO;
import model.component.IInput;
import model.component.IOutput;

public abstract class Mediator {

	protected IOutput sender;
	protected IInput receiver;
	
	public Mediator(IOutput _sender, IInput _receiver) {
		sender = _sender;
		receiver = _receiver;
	}
	
	/**
	 * Envoi des données à un récepteur connu par le médiateur.
	 * @param sender Emetteur
	 * @param data Données
	 */
	public abstract void send (IOutput sender, String data);
}
