package fr.ensicaen.simulator.model.dao.source;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.dao.ScenarioData;

public class XmlSource extends DaoSource {

	private static final long serialVersionUID = 1105996289309239096L;
	private static Logger logger = LoggerFactory.getLogger(XmlSource.class);

	private String path_library_model = "library/model/";
	private String path_library_scenario = "library/scenario/";

	/**
	 * Source de données (read/write)
	 */

	/**
	 * Données effectives en jvm
	 */
	private ScenarioData data;

	/**
	 * Sous classe conteneur des objets devant être serialisés par la source.
	 */

	public XmlSource(Properties props) {
		super(props);
	}

	public void initialize(Properties prop) {
		setPath_library_model(prop.getProperty("config.xml.path.library.model"));
		if (!Files.exists(Paths.get(path_library_model))) {
			Paths.get(path_library_model).toFile().mkdirs();
		}

		path_library_scenario = prop.getProperty("config.xml.path.library.scenario");
		if (!Files.exists(Paths.get(path_library_scenario))) {
			Paths.get(path_library_scenario).toFile().mkdirs();
		}
	}

	public String getPath_library_model() {
		return path_library_model;
	}

	public void setPath_library_model(String path_library_model) {
		this.path_library_model = path_library_model;
	}

	public String getPath_library_scenario() {
		return path_library_scenario;
	}

	public void setPath_library_scenario(String path_library_scenario) {
		this.path_library_scenario = path_library_scenario;
	}

}