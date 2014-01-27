package fr.ensicaen.simulator.model.dao.factory;

import javax.xml.bind.JAXBException;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.dao.impl.xml.ComponentXmlDAO;
import fr.ensicaen.simulator.model.dao.impl.xml.ScenarioDataXmlDAO;
import fr.ensicaen.simulator.model.dao.source.XmlSource;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.tools.Config;

public class XmlDAOFactory extends DAOFactory {
	// @TODO Class de configuration
	protected static XmlSource db = new XmlSource(Config.getProps());

	public DAO<Component> getComponentDAO() {
		return new ComponentXmlDAO(db);
	}

	public DAO<ScenarioData> getScenarioDataDAO() {
		return new ScenarioDataXmlDAO(db);
	}

	public DAO<IStrategy> getStrategyDAO() {
		// @TODO Julien
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean saveTo(ScenarioData d, String path, Class... additionnalJaxbContext) {
		try {
			ScenarioDataXmlDAO.saveTo(d, path, additionnalJaxbContext);
			return true;
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ScenarioData loadFrom(String path, Class... additionnalJaxbContext) {
		try {
			return ScenarioDataXmlDAO.loadFrom(path, additionnalJaxbContext);
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}