package model.strategies;

import model.component.Component;
import model.mediator.Mediator;


public interface IStrategy {

	public void process(Mediator m, String data);
	
}
