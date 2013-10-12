package model.component;

import java.util.ArrayList;
import java.util.List;

import javax.management.ReflectionException;

import model.mediator.Mediator;
import model.memento.Guardian;
import model.response.IResponse;
import model.response.DataResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentIO extends Component implements IInputOutput {
	
	private static Logger log = LoggerFactory.getLogger(ComponentIO.class);
	
	public ComponentIO() {
		// TODO Auto-generated constructor stub
	}

	public ComponentIO(String _name) {
		super(_name);
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public void output(Mediator m, String data) {
		//Guardian.getInstance().addMemento(this, saveState());
		log.debug("[" + this.getName() + "] OUT: " + data);
		m.send(this, data);
		//Guardian.getInstance().addMemento(this, saveState());
	}

	@Override
	public void input(Mediator m, String data) {
		//Guardian.getInstance().addMemento(this, saveState());
		
		//Guardian.getInstance().addMemento(this, saveState());
	}*/

	@Override
	public IResponse input(Mediator m, String data) {
		if(!"send".equals(Thread.currentThread().getStackTrace()[2].getMethodName())) {
			log.error("Invalid call of input method, use mediator instead.");
		}
		
		log.debug("[" + this.getName() + "] IN: '" + data+"'");
		return strategy.processMessage(this, m, data);
	}
	
	@Override
	public String toString() {
		return "C[Input, Output - "+name+"]";
	}
}
