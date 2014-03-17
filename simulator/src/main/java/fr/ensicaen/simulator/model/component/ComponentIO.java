package fr.ensicaen.simulator.model.component;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Context;

@XmlRootElement
public class ComponentIO extends Component implements IInputOutput {

	private static Logger log = LoggerFactory.getLogger(ComponentIO.class);

	public ComponentIO() {
		super();
	}

	public ComponentIO(String _name) {
		super(_name);
	}

	public ComponentIO(String _name, int type) {
		super(_name, type);
	}

	@Override
	public IResponse notifyMessage(Mediator m, String data) {
		if (!"send".equals(Thread.currentThread().getStackTrace()[2].getMethodName())) {
			log.error("Invalid call of input method, use mediator instead.");
		}

		// log.debug("[" + this.getType() + "] IN: '" + data + "'");
		return this.strategy.processMessage(this, m, data);
	}

	@Override
	public void notifyEvent(String event) {
		this.strategy.processEvent(this, event);
	}

	@Override
	public String toString() {
		return "C[Input, Output - " + this.name + "]";
	}

	@Override
	public void init(Context ctx) {
		if (this.strategy != null) {
			this.strategy.init(this, ctx);
		}
	}

	@Override
	public boolean isOutput() {
		return true;
	}

	@Override
	public boolean isInput() {
		return true;
	}

}
