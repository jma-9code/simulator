package model.component;

import java.util.ArrayList;
import java.util.List;

import model.mediator.Mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;

public class ComponentIO extends Component implements IInput, IOutput {
	
	private static Logger log = LoggerFactory.getLogger(ComponentIO.class);
	
	public ComponentIO() {
		// TODO Auto-generated constructor stub
	}

	public ComponentIO(String _name) {
		super(_name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void output(Mediator m, String data) {
		log.debug("[" + this.getName() + "] OUT: " + data);
		m.send(this, data);
	}

	@Override
	public void input(Mediator m, String data) {
		log.debug("[" + this.getName() + "] IN: " + data);
		strategy.inputTreatment(m, data);
	}

}
