package fr.ensicaen.simulator.model.properties;

import java.io.Serializable;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.properties.listener.PropertyListener;
import fr.ensicaen.simulator.simulator.Context;

/**
 * Surcharge de HashMap pour gérer les propriétés de composant avec la notion de
 * clé requise. Cette implémentation intègre en plus des clés insensibles à la
 * casse (toLowerCase).
 * 
 * @author Flo
 * 
 */
@XmlRootElement
public class PropertiesPlus extends HashMap<String, Object> {

	private static Logger log = LoggerFactory.getLogger(PropertiesPlus.class);

	@XmlRootElement
	public static class Property implements Serializable {
		public boolean required = false;
		public String value;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (required ? 1231 : 1237);
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Property other = (Property) obj;
			if (required != other.required)
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			}
			else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return value;
		}

	}

	public PropertiesPlus() {
	}

	/**
	 * Méthode de récupération de la valeur d'une propriété
	 * 
	 * @param key
	 *            Clé
	 * @return Valeur
	 */
	public String get(String key) {
		// case insensitive
		key = key.toLowerCase();

		// si des petits malins mettent des String ... on va les gérer quand
		// même hein!
		Object value = super.get(key);
		if (value instanceof String) {
			put(key, (String) value);
		}

		// abstract impl
		Property prop = (Property) super.get(key);

		if (prop == null) {
			return null;
		}

		// recup listener global
		PropertyListener listener = Context.getInstance().getPropertyListener();

		// valeur surcharge
		String overload = null;

		// event listener invocation
		if (listener == null) {
			log.warn("No global property listener configured in the context.");
		}
		else if (prop.required) {
			overload = listener.onRequiredRead(key, prop.value);
		}
		else {
			overload = listener.onNotRequiredRead(key, prop.value);
		}

		// enregistrement de la valeur
		if (overload != null) {
			prop.value = overload;
		}

		return prop.value;
	}

	/**
	 * Méthode d'insertion d'une propriété non requise.
	 * 
	 * @param key
	 *            Clé
	 * @param value
	 *            Valeur
	 * @return Ancienne valeur
	 */
	public String put(String key, String value) {
		return put(key, value, false);
	}

	/**
	 * Méthode d'insertion d'une propriété.
	 * 
	 * @param key
	 *            Clé
	 * @param value
	 *            Valeur
	 * @param required
	 *            Propriété requise ou non
	 * @return Ancienne valeur
	 */
	public String put(String key, String value, boolean required) {
		Property prop = new Property();
		prop.required = required;
		prop.value = value;

		// comportement hashmap
		Object obj = super.put(key.toLowerCase(), prop);
		if (obj instanceof String) {
			return (String) obj;
		}
		else {
			Property replace = (Property) obj;
			return replace != null ? prop.value : null;
		}
	}
}
