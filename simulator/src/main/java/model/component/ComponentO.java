package model.component;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;

@XmlRootElement
public class ComponentO extends Component implements IOutput {

	private static Logger log = LoggerFactory.getLogger(ComponentO.class);

	public ComponentO() {
		super();
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
		return false;
	}
}
