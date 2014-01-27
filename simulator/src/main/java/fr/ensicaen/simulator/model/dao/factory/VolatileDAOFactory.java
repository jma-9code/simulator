package fr.ensicaen.simulator.model.dao.factory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.dao.impl.memory.GenericVolatileDAO;
import fr.ensicaen.simulator.model.dao.source.VolatileSource;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.tools.Config;

public class VolatileDAOFactory extends DAOFactory {

	protected static VolatileSource db = new VolatileSource(Config.getProps());

	public DAO<Component> getComponentDAO() {
		return new GenericVolatileDAO<Component>(db, Component.class);
	}

	public DAO<IStrategy> getStrategyDAO() {
		return new GenericVolatileDAO<IStrategy>(db, IStrategy.class);
	}

	@Override
	public DAO<ScenarioData> getScenarioDataDAO() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean saveTo(ScenarioData d, String path, Class... additionnalJaxbContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ScenarioData loadFrom(String path, Class... additionnalJaxbContext) {
		// TODO Auto-generated method stub
		return null;
	}

}