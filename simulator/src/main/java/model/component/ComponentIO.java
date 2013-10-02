package model.component;

import java.util.ArrayList;
import java.util.List;

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
	public void output(Component c, String data) {
		if (c instanceof IInput){
			log.debug("[" + this.getName() + "] OUT: " + data + " to [" + c.getName() + "]");
			((IInput) c).input(this, data);
		}else{
			log.warn("[" + c.getName() + "] OUT: " + data + " to [" + c.getName() + "], not possible because this component doesn't support IN data");
		}
	}

	@Override
	public void input(Component c, String data) {
		log.debug("[" + this.getName() + "] IN: " + data + " from [" + c.getName() + "]");
		strategy.inputTreatment(c, data);
	}

}
