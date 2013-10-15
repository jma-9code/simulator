package model.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentO extends Component implements IOutput {

	private static Logger log = LoggerFactory.getLogger(ComponentO.class);

	public ComponentO() {
	}

	public ComponentO(String _name) {
		super(_name);
	}

	@Override
	public void notifyEvent(String event) {
		this.strategy.processEvent(this, event);
	}

	@Override
	public String toString() {
		return "C[Output - " + this.name + "]";
	}
}
