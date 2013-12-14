package model.dao.source;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlSource extends DaoSource {

	private static final long serialVersionUID = 1105996289309239096L;
	private static Logger logger = LoggerFactory.getLogger(XmlSource.class);

	private String path_library_model = "library/model/";

	/**
	 * Source de données (read/write)
	 */

	/**
	 * Données effectives en jvm
	 */
	private XmlData data;

	/**
	 * Sous classe conteneur des objets devant être serialisés par la source.
	 */
	public class XmlData implements Serializable {

		private static final long serialVersionUID = -9088311987427401818L;

		// Centralisation de tout les objets a manipuler.

		public XmlData() {
		}
	}

	public XmlSource(Properties props) {
		super(props);
	}

	public void initialize(Properties prop) {
		setPath_library_model(prop.getProperty("config.xml.path.library.model"));
		if (!Files.exists(Paths.get(path_library_model))) {
			Paths.get(path_library_model).toFile().mkdirs();
		}
	}

	public XmlData getData() {
		return data;
	}

	public String getPath_library_model() {
		return path_library_model;
	}

	public void setPath_library_model(String path_library_model) {
		this.path_library_model = path_library_model;
	}

}