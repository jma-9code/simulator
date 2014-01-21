package utils;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

import ep.strategies.fo.FOUnitTest;

import tools.CaseInsensitiveMap;
import utils.ISO7816Tools.MessageType;

public class ISO8583Tools {

	private static GenericPackager packager = null;
	
	public synchronized static GenericPackager getPackager () {
		if (packager == null) {
			try {
				packager = new GenericPackager(Paths.get(ISO8583Tools.class.getResource("/8583.xml").toURI()).toString());
			}
			catch (ISOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			return packager;
		}
		return packager;
	}
}
