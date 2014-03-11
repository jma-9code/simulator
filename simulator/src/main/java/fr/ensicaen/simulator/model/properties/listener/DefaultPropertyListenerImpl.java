package fr.ensicaen.simulator.model.properties.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPropertyListenerImpl implements PropertyListener {

	private static Logger log = LoggerFactory.getLogger(DefaultPropertyListenerImpl.class);

	@Override
	public String onRequiredRead(String key, String value) {
		if (key == null || key.trim().isEmpty()) {
			log.warn("Component property named " + key + " is empty.");
		}
		return null;
	}

	@Override
	public String onNotRequiredRead(String key, String value) {
		return null;
	}

}
