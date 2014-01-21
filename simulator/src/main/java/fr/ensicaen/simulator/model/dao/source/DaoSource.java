package fr.ensicaen.simulator.model.dao.source;

import java.io.Serializable;
import java.util.Properties;

public abstract class DaoSource implements Serializable {

	/**
         * 
         */
	private static final long serialVersionUID = -8702947837911763440L;
	private boolean initialized = false;

	public DaoSource(Properties props) {
		initialize(props);
	}

	/**
	 * Fonction d'initialisation de la source
	 * 
	 * @param prop
	 *            Config passé à la construction de la source (par défaut la
	 *            config framework)
	 */
	public abstract void initialize(Properties prop);

	public boolean isInitialized() {
		return initialized;
	}

	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

}