package fr.ensicaen.simulator.model.dao.impl.xml;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.source.DaoSource;
import fr.ensicaen.simulator.model.dao.source.XmlSource;
import fr.ensicaen.simulator.model.properties.PropertiesPlus;

public class ComponentXmlDAO extends DAO<Component> {

	@XmlRootElement
	public static class Components {
		@XmlElement
		private List<Component> components;

		public Components() {

		}
	}

	private static Logger logger = LoggerFactory.getLogger(ComponentXmlDAO.class);

	public ComponentXmlDAO(DaoSource _daoSrc) {
		super(_daoSrc);
	}

	@Override
	public boolean create(Component obj) {
		Components data = new Components();
		data.components = obj.getAllTree();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Components.class, PropertiesPlus.Property.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(data, Paths.get(((XmlSource) daoSrc).getPath_library_model(), obj.getUuid())
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
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_model(), obj.getUuid());
		try {
			return Files.deleteIfExists(path);
		}
		catch (IOException e) {
			logger.warn("problem during the delete of " + obj.getUuid(), e);
		}
		return false;
	}

	@Override
	public boolean update(Component obj) {
		delete(obj);
		return create(obj);
	}

	@Override
	public Component find(String id) {
		Component c = null;
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_model());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			JAXBContext jaxbContext = JAXBContext.newInstance(Components.class, PropertiesPlus.Property.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path p = iterator.next();
				if (p.toFile().getName().contains(id)) {
					Components c1 = (Components) jaxbUnmarshaller.unmarshal(p.toFile());
					c = (c1.components != null && !c1.components.isEmpty()) ? c1.components.get(0) : null;
					break;
				}
			}
		}
		catch (IOException | JAXBException e) {
			logger.warn("problem to find uuid : " + id, e);
		}
		return c;
	}

	@Override
	public List<Component> findAll() {
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_model());
		List<Component> components = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			JAXBContext jaxbContext = JAXBContext.newInstance(Components.class, PropertiesPlus.Property.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path p = iterator.next();
				Components c1 = (Components) jaxbUnmarshaller.unmarshal(p.toFile());
				if (c1.components != null && !c1.components.isEmpty())
					components.add(c1.components.get(0));
			}
		}
		catch (IOException | JAXBException e) {
			logger.warn("problem to find all", e);
		}
		return components;
	}

	private static void getAllComponents(Component c, List<Component> know) {
		if (know == null)
			know = new ArrayList<>();

		if (!know.contains(c)) {
			know.add(c);
			for (Component child : c.getChilds()) {
				getAllComponents(child, know);
			}
		}
	}
}