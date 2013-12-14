package model.dao.impl.xml;

import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import model.component.Component;
import model.dao.DAO;
import model.dao.source.DaoSource;
import model.dao.source.XmlSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentXmlDAO extends DAO<Component> {

	private static Logger logger = LoggerFactory.getLogger(ComponentXmlDAO.class);

	public ComponentXmlDAO(DaoSource _daoSrc) {
		super(_daoSrc);
	}

	@Override
	public boolean create(Component obj) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(obj,
					Paths.get(((XmlSource) daoSrc).getPath_library_model(), obj.getName() + "_" + obj.getUuid())
							.toFile());
			return true;
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Component obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Component obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Component find(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Component> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}