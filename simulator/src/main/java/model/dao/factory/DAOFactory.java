package model.dao.factory;

import model.component.Component;
import model.dao.DAO;
import model.dao.ScenarioData;

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