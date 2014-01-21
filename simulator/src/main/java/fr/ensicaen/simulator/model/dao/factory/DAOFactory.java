package fr.ensicaen.simulator.model.dao.factory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.strategies.IStrategy;

public abstract class DAOFactory {
	/**
	 * Article sur l'implémentation du singleton thread-safe par static class
	 * holder
	 * http://embarcaderos.net/2009/06/23/the-singleton-pattern-in-java-multi
	 * -threaded-applications/ => Intéressant à comprendre le problème du
	 * double-checked locking !
	 */
	private static class DAOFactoryHolder {
		public static DAOFactory factory = new XmlDAOFactory();
	}

	public abstract DAO<Component> getComponentDAO();

	public abstract DAO<ScenarioData> getScenarioDataDAO();

	public abstract DAO<IStrategy> getStrategyDAO();

	public abstract boolean saveTo(ScenarioData d, String path);

	public abstract ScenarioData loadFrom(String path);

	/**
	 * Retourne la factory de DAO => localisation du point de changement actuel
	 * de la factory => FLEXIBILITE
	 * 
	 * @return
	 */
	public static DAOFactory getFactory() {
		return DAOFactoryHolder.factory;
	}

}