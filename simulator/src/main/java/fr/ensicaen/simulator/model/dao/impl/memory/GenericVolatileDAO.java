package fr.ensicaen.simulator.model.dao.impl.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.source.DaoSource;
import fr.ensicaen.simulator.model.dao.source.VolatileSource;

public class GenericVolatileDAO<T> extends DAO<T> {

	private static Logger logger = LoggerFactory.getLogger(GenericVolatileDAO.class);

	private Map<String, T> db;

	public GenericVolatileDAO(DaoSource _daoSrc, Class<T> clazz) {
		super(_daoSrc);
		db = (Map<String, T>) ((VolatileSource) getDaoSrc()).getDb(clazz);
	}

	@Override
	public boolean create(T obj) {
		db.put(String.valueOf(System.identityHashCode(obj)), obj);
		return true;
	}

	@Override
	public boolean delete(T obj) {
		db.remove(obj);
		return true;
	}

	@Override
	public boolean update(T obj) {
		return create(obj);
	}

	@Override
	public T find(String id) {
		return db.get(id);
	}

	@Override
	public List<T> findAll() {
		return new ArrayList(db.values());
	}

}
