package fr.ensicaen.simulator_ep.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

public class ISOTools {

	/**
	 * Parse l'iso message Permet de recuperer la liste des champs sous la
	 * structure suivante: List<List<String>>, List<id, valeur, description>
	 * 
	 * @param msg
	 * @param pack
	 * @return
	 * @throws ISOException
	 */
	public static List<List<String>> readISOMsg(String msg, ISOPackager pack) throws ISOException {
		ISOMsg isomsg = new ISOMsg();
		isomsg.setPackager(pack);
		isomsg.unpack(msg.getBytes());

		List<List<String>> data = new ArrayList<>();
		data.add(Arrays.asList("0", isomsg.getMTI(), "MTI"));
		for (int i = 1; i <= isomsg.getMaxField(); i++) {
			if (isomsg.hasField(i)) {
				data.add(Arrays.asList("" + i, isomsg.getString(i), pack.getFieldDescription(isomsg, i)));
			}
		}
		return data;
	}
}
