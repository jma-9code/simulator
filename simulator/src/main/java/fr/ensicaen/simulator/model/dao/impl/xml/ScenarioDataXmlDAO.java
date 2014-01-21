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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.dao.source.DaoSource;
import fr.ensicaen.simulator.model.dao.source.XmlSource;

public class ScenarioDataXmlDAO extends DAO<ScenarioData> {

	private static Logger logger = LoggerFactory.getLogger(ScenarioDataXmlDAO.class);

	public ScenarioDataXmlDAO(DaoSource _daoSrc) {
		super(_daoSrc);
	}

	@Override
	public boolean create(ScenarioData obj) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// jaxbMarshaller.setAdapter(Component.class, new
			// ComponentAdapter());
			jaxbMarshaller.marshal(obj, Paths.get(((XmlSource) daoSrc).getPath_library_scenario(), obj.getName())
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
	public boolean delete(ScenarioData obj) {
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_scenario(), obj.getName());
		try {
			return Files.deleteIfExists(path);
		}
		catch (IOException e) {
			logger.warn("problem during the delete of " + obj.getName(), e);
		}
		return false;
	}

	@Override
	public boolean update(ScenarioData obj) {
		delete(obj);
		return create(obj);
	}

	@Override
	public ScenarioData find(String id) {
		ScenarioData c = null;
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_scenario());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			JAXBContext jaxbContext = JAXBContext.newInstance(ScenarioData.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path p = iterator.next();
				if (p.toFile().getName().equals(id)) {
					c = (ScenarioData) jaxbUnmarshaller.unmarshal(p.toFile());
					break;
				}
			}
		}
		catch (IOException | JAXBException e) {
			logger.warn("problem to find id : " + id, e);
		}
		return c;
	}

	@Override
	public List<ScenarioData> findAll() {
		Path path = Paths.get(((XmlSource) daoSrc).getPath_library_scenario());
		List<ScenarioData> scenarios = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			JAXBContext jaxbContext = JAXBContext.newInstance(ScenarioData.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path p = iterator.next();
				scenarios.add((ScenarioData) jaxbUnmarshaller.unmarshal(p.toFile()));
			}
		}
		catch (IOException | JAXBException e) {
			logger.warn("problem to find all", e);
		}
		return scenarios;
	}

	/**
	 * Get scenario from specific file
	 * 
	 * @param path
	 * @return
	 * @throws JAXBException
	 */
	public static ScenarioData loadFrom(String path) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ScenarioData.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (ScenarioData) jaxbUnmarshaller.unmarshal(Paths.get(path).toFile());
	}

	/**
	 * Put scenario in specific file
	 * 
	 * @param d
	 * @param path
	 * @throws JAXBException
	 */
	public static void saveTo(ScenarioData d, String path) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(d.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(d, Paths.get(path).toFile());
	}
}