package fr.ensicaen.simulator.tools;

import java.util.HashMap;

public class Utils {

	/**
	 * Convert key:val;key:val to hashmap
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, String> string2Hashmap(String data) {
		HashMap<String, String> ret = new HashMap<>();
		String[] d = data.split(";");
		String[] pair;
		for (String s : d) {
			pair = s.split(":");
			ret.put(pair[0], pair[1]);
		}
		return ret;
	}

}
