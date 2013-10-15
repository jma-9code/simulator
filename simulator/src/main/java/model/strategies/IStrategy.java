package model.strategies;

import model.component.Component;
import model.mediator.Mediator;
import model.response.IResponse;

public interface IStrategy<T extends Component> {

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
