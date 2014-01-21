package fr.ensicaen.simulator_ep.utils;

import java.net.URISyntaxException;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

public class ISO8583Tools {

	private static GenericPackager packager = null;

	public synchronized static GenericPackager getPackager() {
		if (packager == null) {
			try {
				packager = new GenericPackager(ISO8583Tools.class.getResource("/8583.xml").toURI().getPath());
			}
			catch (ISOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;
			return packager;
		}
		return packager;
	}
}
