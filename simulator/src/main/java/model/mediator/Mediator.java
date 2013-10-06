package model.mediator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.component.Component;
import model.component.ComponentIO;
import model.component.IInput;
import model.component.IOutput;

public abstract class Mediator {

	protected IOutput sender;
	protected IInput receiver;
	
	private HashMap<String, String> properties = null;
	
	
	public Mediator(IOutput _sender, IInput _receiver) {
		properties = new HashMap<>();
		sender = _sender;
		receiver = _receiver;
	}
	
	/**
	 * Envoi des données à un récepteur connu par le médiateur.
	 * @param sender Emetteur
	 * @param data Données
	 */
	public abstract void send (IOutput sender, String data);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((receiver == null) ? 0 : receiver.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mediator other = (Mediator) obj;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		return true;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
}
