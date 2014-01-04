package model.dao.factory;

import model.component.Component;
import model.dao.DAO;
import model.dao.ScenarioData;
import model.dao.impl.xml.ComponentXmlDAO;
import model.dao.impl.xml.ScenarioDataXmlDAO;
import model.dao.source.XmlSource;
import tools.Config;

public class XmlDAOFactory extends DAOFactory {
	// @TODO Class de configuration
	protected static XmlSource db = new XmlSource(Config.getProps());

	public DAO<Component> getComponentDAO() {
		return new ComponentXmlDAO(db);
	}

	public DAO<ScenarioData> getScenarioDataDAO() {
		return new ScenarioDataXmlDAO(db);
	}

}