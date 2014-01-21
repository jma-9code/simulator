package model.component;

import javax.xml.bind.annotation.XmlSeeAlso;

import model.mediator.Mediator;
import model.response.IResponse;

@XmlSeeAlso({ ComponentIO.class, ComponentI.class })
public interface IInput {
	/**
	 * Retourne le nom de l'élément.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Retourne l'acronyme de l'élément.
	 * 
	 * @return
	 */
	public String getAcronym();

	/**
	 * Receipt notification of an input invocation
	 * 
	 * @param m
	 * @param data
	 * @return
	 */
	public IResponse notifyMessage(Mediator m, String data);

}
