package tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ISO7816Exception;
import utils.ISO7816Tools;

public class ISO7816 {
	private static Logger log = LoggerFactory.getLogger(ISO7816.class);

	/**
	 * Compare les elements du msg 7816. Verifie que le msg est bien en 7816.
	 * 
	 * @param expected
	 * @param actual
	 * @param keys
	 * @return
	 * @throws ISO7816Exception
	 */
	public static boolean compareIso7816(String expected, String actual, String... keys) throws ISO7816Exception {
		Map<String, String> expect = ISO7816Tools.read(expected);
		Map<String, String> act = ISO7816Tools.read(actual);
		String a = null;
		String b = null;
		for (String s : keys) {
			a = expect.get(keys);
			b = act.get(keys);
			if (a != null && b != null && !a.equalsIgnoreCase(b)) {
				log.warn("field not equals : " + s + " (" + a + "," + b + ")");
				return false;
			}
		}
		return true;
	}
}
