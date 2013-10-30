package model.component;

import model.mediator.Mediator;
import model.response.IResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;

public class ComponentIO extends Component implements IInputOutput {

	private static Logger log = LoggerFactory.getLogger(ComponentIO.class);

	public ComponentIO(String _name) {
		super(_name);
	}

	@Override
	public IResponse notifyMessage(Mediator m, String data) {
		if (!"send".equals(Thread.currentThread().getStackTrace()[2].getMethodName())) {
			log.error("Invalid call of input method, use mediator instead.");
		}

		log.debug("[" + this.getName() + "] IN: '" + data + "'");
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
		this.strategy.init(this, ctx);
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
