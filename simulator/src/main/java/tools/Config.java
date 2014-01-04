package tools;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	private static Properties props = null;

	/**
	 * Initialisation static de la classe. Permet de charger les attribus classe
	 * a partir du fichier Config. En cas d'erreur on laisse les parametres par
	 * defaut.
	 */
	static {
		FileInputStream configFile = null;
		try {
			configFile = new FileInputStream("config.properties");
			props = new Properties();
			props.load(configFile);
			configFile.close();
		}
		catch (Exception e) {
			defaultproperties();
		}
	}

	public static void defaultproperties() {
		System.out.println("Lancement de la configuration par defaut");
		props = new Properties();
		props.put("config.xml.path.library.model", "./library/model/");
		props.put("config.xml.path.library.scenario", "./library/scenario/");
	}

	public static Properties getProps() {
		return props;
	}
}
