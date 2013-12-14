package model.dao.factory;

import model.component.Component;
import model.dao.DAO;
import model.dao.impl.xml.ComponentXmlDAO;
import model.dao.source.XmlSource;
import tools.Config;

public class XmlDAOFactory extends DAOFactory {
	// @TODO Class de configuration
	protected static XmlSource db = new XmlSource(Config.getProps());

	public DAO<Component> getComponentDAO() {
		return new ComponentXmlDAO(db);
	}

}