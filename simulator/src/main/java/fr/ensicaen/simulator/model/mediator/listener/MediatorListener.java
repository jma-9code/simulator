package fr.ensicaen.simulator.model.mediator.listener;

import fr.ensicaen.simulator.model.component.IOutput;

public interface MediatorListener {

	/**
	 * Invoquée lorsque le mediator est utilisé pour envoyer des donnees.
	 */
	public void onSendData(IOutput sender, String data);

}
