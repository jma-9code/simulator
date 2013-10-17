package model.component;

import model.mediator.Mediator;
import model.response.IResponse;

public interface IInput {
	/**
	 * Retourne le nom de l'élément.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Receipt notification of an input invocation
	 * 
	 * @param m
	 * @param data
	 * @return
	 */
	public IResponse notifyMessage(Mediator m, String data);

}
