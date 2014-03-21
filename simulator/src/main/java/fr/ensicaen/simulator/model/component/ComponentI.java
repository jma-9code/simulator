package fr.ensicaen.simulator.model.component;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;

@XmlRootElement
public class ComponentI extends Component implements IInput {

	private static Logger log = LoggerFactory.getLogger(ComponentI.class);

	public ComponentI() {
		super();
	}

	public ComponentI(String _name) {
		super(_name);
	}

	public ComponentI(String _name, int type) {
		super(_name, type);
	}

	@Override
	public IResponse notifyMessage(Mediator m, String data) {
		if (!"send".equals(Thread.currentThread().getStackTrace()[2].getMethodName())) {
			log.warn("Invalid call of input method, use mediator instead.");
		}

		return this.strategy.processMessage(this, m, data);
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
