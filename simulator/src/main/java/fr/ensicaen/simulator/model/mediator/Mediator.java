package fr.ensicaen.simulator.model.mediator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.InputAdapter;
import fr.ensicaen.simulator.model.dao.jaxbadapter.OutputAdapter;
import fr.ensicaen.simulator.model.response.IResponse;

@XmlSeeAlso({ SimplexMediator.class, ReverseHalfDuplexMediator.class, ForwardMediator.class, PipedMediator.class,
		HalfDuplexMediator.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Mediator implements Serializable {

	@XmlAttribute
	@XmlID
	protected String uuid;

	@XmlJavaTypeAdapter(OutputAdapter.class)
	protected IOutput sender;

	@XmlJavaTypeAdapter(InputAdapter.class)
	protected IInput receiver;

	private Map<String, String> properties = null;

	protected transient CyclicBarrier barrier = new CyclicBarrier(1);

	public Mediator() {
	}

	public Mediator(IOutput _sender, IInput _receiver) {
		this.properties = new HashMap<>();
		this.sender = _sender;
		this.receiver = _receiver;
		this.uuid = "m-" + UUID.randomUUID().toString();
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

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public IOutput getSender() {
		return this.sender;
	}

	public IInput getReceiver() {
		return this.receiver;
	}

	public String getUuid() {
		return uuid;
	}

	public CyclicBarrier getBarrier() {
		return barrier;
	}

	public void setBarrier(CyclicBarrier barrier) {
		this.barrier = barrier;
	}
}
