package fr.ensicaen.simulator.model.strategies;

import java.io.Serializable;
import java.util.List;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Context;

public interface IStrategy<T extends Component> extends Serializable {

	/**
	 * Liste des définitions des propriétés utilisés par la stratégie permettant
	 * l'initialisation des propriétés du composants.
	 */
	public List<PropertyDefinition> getPropertyDefinitions();

	/**
	 * Phase d'initialisation d'une stratégie dans laquelle le composant devra
	 * s'enregistrer auprès des évènements pour lesquels il souhaite être
	 * notifié.
	 * 
	 * @param _this
	 * @param ctx
	 */
	public void init(IOutput _this, Context ctx);

	/**
	 * Implémentable par ComponentIO et ComponentO
	 * 
	 * Délégation du traitement des évènements
	 * 
	 * @param _this
	 * @param event
	 */
	public void processEvent(T _this, String event);

	/**
	 * Implémentable par ComponentIO et ComponentI
	 * 
	 * Délégation du traitement des messages entrants
	 * 
	 * @param _this
	 * @param event
	 */
	public IResponse processMessage(T _this, Mediator mediator, String data);

}
