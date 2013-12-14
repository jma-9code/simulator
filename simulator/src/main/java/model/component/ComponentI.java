package model.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import model.mediator.Mediator;
import model.response.VoidResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentI extends Component implements IInput {

	private static Logger log = LoggerFactory.getLogger(ComponentI.class);

	public ComponentI() {
		super();
	}

	public ComponentI(String _name) {
		super(_name);
	}

	@Override
	public VoidResponse notifyMessage(Mediator m, String data) {
		// Guardian.getInstance().addMemento(this, saveState());
		// log.debug("[" + this.getName() + "] IN: " + data + " from [" +
		// c.getName() + "]");
		this.strategy.processMessage(this, m, data);
		// Guardian.getInstance().addMemento(this, saveState());

		return VoidResponse.build();
	}

	@Override
	public String toString() {
		return "C[Input - " + this.name + "]";
	}

	@Override
	public boolean isOutput() {
		return false;
	}

	@Override
	public boolean isInput() {
		return true;
	}

}
