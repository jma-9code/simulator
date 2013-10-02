package model.strategies;

import model.component.Component;
import model.mediator.Mediator;


public interface IStrategy {

	public void inputTreatment (Mediator m, String data);
	
	public void outputTreatment (Mediator m, String data);	
	
}
