package model.dao.factory;

import model.component.Component;
import model.dao.DAO;
import model.dao.impl.memory.GenericVolatileDAO;
import model.dao.source.VolatileSource;
import model.strategies.IStrategy;
import tools.Config;

public class VolatileDAOFactory extends DAOFactory {

	protected static VolatileSource db = new VolatileSource(Config.getProps());

	public DAO<Component> getComponentDAO() {
		return new GenericVolatileDAO<Component>(db, Component.class);
	}

	public DAO<IStrategy> getStrategyDAO() {
		return new GenericVolatileDAO<IStrategy>(db, IStrategy.class);
	}

}