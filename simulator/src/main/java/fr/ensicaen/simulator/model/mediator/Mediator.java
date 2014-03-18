package fr.ensicaen.simulator.model.mediator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.InputAdapter;
import fr.ensicaen.simulator.model.dao.jaxbadapter.OutputAdapter;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.properties.PropertiesPlus;
import fr.ensicaen.simulator.model.response.IResponse;

@XmlSeeAlso({ SimplexMediator.class, ReverseHalfDuplexMediator.class, ForwardMediator.class, PipedMediator.class,
		HalfDuplexMediator.class, ChildHalfDuplexMediator.class, ChildSimplexMediator.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Mediator implements Serializable {

	protected static Logger log = LoggerFactory.getLogger(Mediator.class);

	@XmlAttribute
	@XmlID
	protected String uuid;

	@XmlJavaTypeAdapter(OutputAdapter.class)
	protected IOutput sender;

	@XmlJavaTypeAdapter(InputAdapter.class)
	protected IInput receiver;

	protected PropertiesPlus properties;

	protected transient Set<MediatorListener> listeners = new HashSet<>();

	public Mediator() {
	}

	public Mediator(IOutput _sender, IInput _receiver) {
		this.properties = new PropertiesPlus();
		this.sender = _sender;
		this.receiver = _receiver;
		this.uuid = _sender.getUuid() + "-" + _receiver.getUuid();
	}

	/**
	 * Set the protocol used by mediator.
	 * 
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		properties.put("protocol", protocol);
	}

	/**
	 * Ajouter un listener au mediator
	 * 
	 * @param list
	 */
	public void addListener(MediatorListener list) {
		listeners.add(list);
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

	public PropertiesPlus getProperties() {
		return this.properties;
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

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + sender.getName() + " -> " + receiver.getName();
	}

}
