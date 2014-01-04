package model.component;

import javax.xml.bind.annotation.XmlSeeAlso;

import simulator.Context;

@XmlSeeAlso({ ComponentIO.class, ComponentO.class })
public interface IOutput {
	/**
	 * Retourne le nom de l'élément.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Event notification.
	 * 
	 * @param event
	 */
	public void notifyEvent(String event);

	/**
	 * Initialization process
	 * 
	 * @param ctx
	 */
	public void init(Context ctx);
}
