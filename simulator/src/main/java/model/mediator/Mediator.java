package model.mediator;

import java.io.Serializable;
import java.util.HashMap;

import model.component.IInput;
import model.component.IOutput;
import model.response.IResponse;

public abstract class Mediator implements Serializable {

	protected IOutput sender;
	protected IInput receiver;

	private HashMap<String, String> properties = null;

	public Mediator(IOutput _sender, IInput _receiver) {
		this.properties = new HashMap<>();
		this.sender = _sender;
		this.receiver = _receiver;
	}

	/**
	 * Envoi des données à un récepteur connu par le médiateur.
	 * 
	 * @param sender
	 *            Emetteur
	 * @param data
	 *            Données
	 */
	public abstract IResponse send(IOutput sender, String data);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.receiver == null) ? 0 : this.receiver.hashCode());
		result = prime * result + ((this.sender == null) ? 0 : this.sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Mediator other = (Mediator) obj;
		if (this.receiver == null) {
			if (other.receiver != null) {
				return false;
			}
		}
		else if (!this.receiver.equals(other.receiver) && !this.receiver.equals(other.sender)) {
			return false;
		}
		if (this.sender == null) {
			if (other.sender != null) {
				return false;
			}
		}
		else if (!this.sender.equals(other.sender) && !this.sender.equals(other.receiver)) {
			return false;
		}
		return true;
	}

	public HashMap<String, String> getProperties() {
		return this.properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public IOutput getSender() {
		return this.sender;
	}

	public IInput getReceiver() {
		return this.receiver;
	}
}
