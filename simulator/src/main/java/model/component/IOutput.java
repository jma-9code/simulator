package model.component;

import model.mediator.Mediator;


public interface IOutput {

	//j'envoie des donnees vers c
	public void output(Mediator m, String data);
}
