package model.strategies;

import model.component.Component;
import model.mediator.Mediator;


public interface IStrategy {

	public void process(Component component, Mediator mediator, String data);
	
}
