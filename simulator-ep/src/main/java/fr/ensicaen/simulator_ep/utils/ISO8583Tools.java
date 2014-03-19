package fr.ensicaen.simulator_ep.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.simulator.Context;

public class ISO8583Tools {

	private static Map<String, GenericPackager> packagers = new HashMap<String, GenericPackager>();
	private static Logger log = LoggerFactory.getLogger(ISO8583Tools.class);

	public synchronized static GenericPackager getPackager(String packagerPath) {
		GenericPackager packager = packagers.get(packagerPath);

		if (packager == null) {

			try {
				packager = new GenericPackager(ISO8583Tools.class.getResource(packagerPath).toURI().getPath());
			}
			catch (Exception e) {
				log.error("Error while loading packager " + packagerPath, e);
			}

			// save instance
			packagers.put(packagerPath, packager);
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
		rp.setPackager(getPackager("/ISO8583.xml"));
		try {
			rp.unpack(data.getBytes());
		}
		catch (ISOException e) {
			throw new ISO8583Exception(e);
		}
		return rp;
	}

	public static ISOMsg read_CB2A_TLC(String data) throws ISO8583Exception {
		ISOMsg rp = new ISOMsg();
		rp.setPackager(getPackager("/CB2A_TLC.xml"));
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
		rp.setPackager(getPackager("/ISO8583.xml"));
		return rp;
	}

	public static ISOMsg create_CB2A_TLC() {
		ISOMsg rp = new ISOMsg();
		rp.setPackager(getPackager("/CB2A_TLC.xml"));
		return rp;
	}

	/**
	 * Retourne la date au format 8583 : MMJJ
	 * 
	 * @return
	 */
	public static String getDate_MMJJ() {
		DateFormat format = new SimpleDateFormat("MMDD");
		return format.format(Context.getInstance().getTime());
	}

	/**
	 * Retourne l'heure au format 8583 : hhmmss
	 * 
	 * @return
	 */
	public static String getDate_hhmmss() {
		DateFormat format = new SimpleDateFormat("hhmmss");
		return format.format(Context.getInstance().getTime());
	}

	/**
	 * Formate une chaine de la taille 'size' avec le caractère 'padding' en
	 * bourrage.
	 * 
	 * @param value
	 * @param size
	 * @param padding
	 * @return
	 */
	public static String paddingLeft(String value, int size, char padding) {
		if (size < 1) {
			return "";
		}

		StringBuilder build = new StringBuilder();
		int paddingSize = size;

		if (value != null) {
			if (value.length() > size) {
				build.append(value.substring(0, size));
				paddingSize = size - value.length();
			}
			else {
				build.append(value);
				paddingSize = size - value.length();
			}
		}

		// padding process
		for (int i = 0; i < paddingSize; i++) {
			build.insert(0, padding);
		}

		return build.toString();
	}

	public static void main(String[] args) {
		System.out.println(paddingLeft("123456789", 11, '0'));
	}

	/**
	 * Retourne l'heure au format 8583 : MMJJhhmmss
	 * 
	 * @return
	 */
	public static String getDate_MMJJhhmmss() {
		DateFormat format = new SimpleDateFormat("MMDDhhmmss");
		return format.format(Context.getInstance().getTime());
	}
}
