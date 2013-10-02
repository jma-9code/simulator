package model.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.component.Component;
import model.component.ComponentIO;

public class TPEStrategy implements IStrategy {

	private static Logger log = LoggerFactory.getLogger(TPEStrategy.class);
	
	private ComponentIO tpe;
	
	public TPEStrategy(ComponentIO _tpe) {
		tpe = _tpe;
	}

	@Override
	public String inputTreatment(Component c, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void outputTreatment(Component c, String data) {
		// TODO Auto-generated method stub
		
	}

}
