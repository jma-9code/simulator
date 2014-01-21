package fr.ensicaen.simulator.tools;

import java.util.HashMap;

public class CaseInsensitiveMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1989935104727840795L;

	@Override
	public String put(String key, String value) {
		return super.put(key.toLowerCase(), value);
	}

	@Override
	public String get(Object key) {
		return super.get(((String) key).toLowerCase());
	}
}
