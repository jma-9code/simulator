package model.component;

import java.util.ArrayList;
import java.util.List;

import model.mediator.Mediator;
import model.memento.Guardian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentO extends Component implements IOutput {
	
	private static Logger log = LoggerFactory.getLogger(ComponentO.class);
	
	public ComponentO() {
		// TODO Auto-generated constructor stub
	}

	public ComponentO(String _name) {
		super(_name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void output(Mediator m, String data) {
		//Guardian.getInstance().addMemento(this, saveState());
		log.debug("[" + this.getName() + "] OUT: " + data);
		m.send(this, data);
		//Guardian.getInstance().addMemento(this, saveState());
	}

}
