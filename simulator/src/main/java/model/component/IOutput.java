package model.component;

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
}
