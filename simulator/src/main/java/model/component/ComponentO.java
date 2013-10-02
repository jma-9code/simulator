package model.component;

import java.util.ArrayList;
import java.util.List;

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
	public void output(Component c, String data) {
		if (c instanceof IInput){
			log.debug("[" + this.getName() + "] OUT: " + data + " to [" + c.getName() + "]");
			((IInput) c).input(this, data);
		}else{
			log.warn(c.getName() + " OUT: " + data + " to " + c.getName() + ", not possible because this component doesn't support IN data");
		}
	}

}
