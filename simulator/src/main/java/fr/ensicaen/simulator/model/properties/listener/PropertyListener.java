package fr.ensicaen.simulator.model.properties.listener;

public interface PropertyListener {

	/**
	 * Invoquée lorsqu'une propriété requise est lue.
	 * 
	 * @param key
	 * @return Valeur surchargée ou null
	 */
	public String onRequiredRead(String key, String value);

	/**
	 * Invoquée lorsqu'une propriété non requis est lue
	 * 
	 * @param key
	 * @return Valeur surchargée ou null
	 */
	public String onNotRequiredRead(String key, String value);

}
