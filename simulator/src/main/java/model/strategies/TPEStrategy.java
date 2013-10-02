package model.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.component.Component;
import model.component.ComponentIO;
import model.mediator.Mediator;

public class TPEStrategy implements IStrategy {

	private static Logger log = LoggerFactory.getLogger(TPEStrategy.class);
	
	private ComponentIO tpe;
	
	public TPEStrategy(ComponentIO _tpe) {
		tpe = _tpe;
	}

	@Override
	public void inputTreatment(Mediator c, String data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void outputTreatment(Mediator c, String data) {
		// TODO Auto-generated method stub
		
	}

}
