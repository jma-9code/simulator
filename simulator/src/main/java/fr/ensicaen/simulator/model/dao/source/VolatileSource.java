package fr.ensicaen.simulator.model.dao.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolatileSource extends DaoSource {

	private static final long serialVersionUID = 1105996289309239096L;
	private static Logger logger = LoggerFactory.getLogger(VolatileSource.class);

	private Map<Class<?>, Map<String, Object>> dbs;

	public VolatileSource(Properties props) {
		super(props);
	}

	@Override
	public void initialize(Properties prop) {
		dbs = new HashMap<>();
	}

	/**
	 * Retrieve the volatile database for a object type
	 * 
	 * @param clazz
	 * @return
	 */
	public Map<String, Object> getDb(Class<?> clazz) {
		Map<String, Object> db = dbs.get(clazz);

		if (db == null) {
			db = new HashMap<String, Object>();
			dbs.put(clazz, db);
		}

		return db;
	}
}