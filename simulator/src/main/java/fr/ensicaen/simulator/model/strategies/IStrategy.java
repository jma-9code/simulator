package fr.ensicaen.simulator.model.strategies;

import java.io.Serializable;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Context;

public interface IStrategy<T extends Component> extends Serializable {

	/**
	 * Phase d'initialisation d'une stratégie dans laquelle le composant devra
	 * s'enregistrer auprès des évènements pour lesquels il souhaite être
	 * notifié.
	 * 
	 * @param _this
	 *            TODO
	 * @param ctx
	 */
	public void init(IOutput _this, Context ctx);

	// need thinking.
	// on pourrait imaginer traiter des évènements globaux et spécifiques
	// (par composant avec relation d'héritage pr les composites)
	// et cela serait enregistré dans le contexte (class Context)
	//
	// On modifierai ensuite le simulateur afin que les points de démarrage soit
	// des évènements
	// et non une données dans un input. Ex : J'insère ma carte sur le composant
	// TPE.

	public void processEvent(T _this, String event);

	public IResponse processMessage(T _this, Mediator mediator, String data);
}
