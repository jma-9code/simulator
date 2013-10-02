package model.component;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentI extends Component implements IInput {

	private static Logger log = LoggerFactory.getLogger(ComponentI.class);
	
	public ComponentI() {
		// TODO Auto-generated constructor stub
	}

	public ComponentI(String _name) {
		super(_name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void input(Component c, String data) {
		log.debug("[" + this.getName() + "] IN: " + data + " from [" + c.getName() + "]");
		strategy.inputTreatment(c, data);
	}


}
