package model.dao;

import java.util.List;

import model.dao.source.DaoSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DAO<T> {

	private static Logger logger = LoggerFactory.getLogger(DAO.class);

	protected DaoSource daoSrc = null;

	/**
	 * Constructeur
	 */
	public DAO(DaoSource _daoSrc) {
		daoSrc = _daoSrc;
		logger.debug(this.getClass().getName() + " instanciated");
	}

	/**
	 * Constructeur
	 */
	public DAO() {
		// daoSrc = _daoSrc;
		logger.debug(this.getClass().getName() + " instanciated");
	}

	/**
	 * Méthode de création
	 * 
	 * @param obj
	 * @return
	 */
	public abstract boolean create(T obj);

	/**
	 * Méthode pour effacer
	 * 
	 * @param obj
	 * @return
	 */
	public abstract boolean delete(T obj);

	/**
	 * Méthode de mise à jour
	 * 
	 * @param obj
	 * @return
	 */
	public abstract boolean update(T obj);

	/**
	 * Méthode de recherche des informations
	 * 
	 * @param id
	 * @return
	 */
	public abstract T find(String id);

	/**
	 * Méthode de recherche totale
	 * 
	 * @param id
	 * @return
	 */
	public abstract List<T> findAll();

	/**
	 * Renvoi la source de persistence
	 * 
	 * @return
	 */
	public DaoSource getDaoSrc() {
		return daoSrc;
	}
}