package fr.ensicaen.simulator_ep.utils;

import java.net.URISyntaxException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

public class ISO8583Tools {

	private static GenericPackager packager = null;

	public synchronized static GenericPackager getPackager() {
		if (packager == null) {
			try {
				packager = new GenericPackager(ISO8583Tools.class
						.getResource("/" + ProtocolEP.ISO8583.toString() + ".xml").toURI().getPath());
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

	/**
	 * Transform string to ISOMsg
	 * 
	 * @param data
	 * @return
	 * @throws ISO7816Exception
	 */
	public static ISOMsg read(String data) throws ISO8583Exception {
		ISOMsg rp = new ISOMsg();
		rp.setPackager(getPackager());
		try {
			rp.unpack(data.getBytes());
		}
		catch (ISOException e) {
			throw new ISO8583Exception(e);
		}
		return rp;
	}

	/**
	 * Create ISOMsg from specific normalization
	 * 
	 * @return
	 */
	public static ISOMsg create() {
		ISOMsg rp = new ISOMsg();
		rp.setPackager(getPackager());
		return rp;
	}
}
