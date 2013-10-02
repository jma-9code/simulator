package model.strategies;

import model.component.Component;


public interface IStrategy {

	public String inputTreatment (Component c, String data);
	
	public void outputTreatment (Component c, String data);	
	
}
