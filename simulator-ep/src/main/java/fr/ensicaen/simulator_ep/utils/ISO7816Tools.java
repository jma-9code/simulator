package fr.ensicaen.simulator_ep.utils;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

public class ISO7816Tools {

	private static GenericPackager packager = null;

	public synchronized static GenericPackager getPackager() {
		if (packager == null) {
			try {
				packager = new GenericPackager(ISO8583Tools.class.getResource("/7816.xml").toURI().getPath());
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

	public enum MessageType {
		SECURE_CHANNEL_RQ, SECURE_CHANNEL_RP, CARDHOLDER_AUTH_RQ, CARDHOLDER_AUTH_RP, AUTHORIZATION_RP_CRYPTO, TRANSCATION_VAL_NOTIF, TRANSACTION_VAL_ACK, UNKNOWN_TYPE;
	}

	// champs utilises dans la 7816
	public static final int FIELD_POSID = 2;
	public static final int FIELD_PROTOCOLLIST = 4;
	public static final int FIELD_PROTOCOL = 5;
	public static final int FIELD_DATETIME = 14;
	public static final int FIELD_PROTOCOLPREFERRED = 9;
	public static final int FIELD_AMOUNT = 6;
	public static final int FIELD_OPCODE = 10;
	public static final int FIELD_PINDATA = 11;
	public static final int FIELD_PINVERIFICATION = 13;
	public static final int FIELD_CARDAGREEMENT = 7;
	public static final int FIELD_PAN = 3;
	public static final int FIELD_APPROVALCODE = 8;
	public static final int FIELD_STAN = 12;//
	public static final int FIELD_RRN = 15;// transcaction ID
	public static final int FIELD_RESPONSECODE = 16;

	/**
	 * Permet de faire STAN+1
	 * 
	 * @param stan
	 * @return
	 */
	public static String generateSTAN(String stan) {
		String ret = null;
		int val_stan = Integer.parseInt(stan);
		if (val_stan > 999999) {
			ret = "000001";
		}
		else {
			ret = String.format("%06d", ++val_stan);
		}
		return ret;
	}

	/**
	 * Ajoute le padding à gauche de 0 (max 16 caracteres). ex : POS IS ->
	 * 0000000000POS ID
	 * 
	 * @param tag
	 * @return
	 */
	public static String writeTAG(String tag) {
		return ("0000000000000000" + tag).substring(tag.length());
	}

	/**
	 * Ajoute le padding à gauche de 0 (max 3 caracteres). ex : 2 -> 002
	 * 
	 * @param tag
	 * @return
	 */
	public static String writeLEN(int len) {
		return String.format("%03d", len);
	}

	/**
	 * Permet de formater le champ DATETIME de la norme
	 * 
	 * @param d
	 * @return
	 */
	public static String writeDATETIME(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		return sdf.format(d);
	}

	/**
	 * Permet de recuperer la valeur du montant en double
	 * 
	 * @param d
	 * @return
	 */
	public static double readAMOUNT(String d) {
		StringBuffer sb = new StringBuffer(d);
		sb.insert(8, '.');
		return Double.parseDouble(sb.toString());
	}

	/**
	 * Permet d'ecrire la valeur du montant en double en 7816
	 * 
	 * @param d
	 * @return
	 */
	public static String writeAMOUNT(double d) {
		String r = String.format("%.2f", d);
		r = r.replace(",", "");
		return ("0000000000" + r).substring(r.length());
	}

	public static String createformatTLV(String tag, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(writeTAG(tag));
		sb.append(writeLEN(value.length()));
		sb.append(value);

		return sb.toString();
	}

	/**
	 * Permet de convertir le code message vers le type du message. Ex : 0101 ->
	 * SECURE_CHANNEL_RQ
	 * 
	 * @param head
	 * @return
	 */
	public static MessageType convertCodeMsg2Type(String head) {
		MessageType type = MessageType.UNKNOWN_TYPE;
		switch (head) {
			case "0101":
				type = MessageType.SECURE_CHANNEL_RQ;
				break;
			case "0110":
				type = MessageType.SECURE_CHANNEL_RP;
				break;
			case "0301":
				type = MessageType.CARDHOLDER_AUTH_RQ;
				break;
			case "0310":
				type = MessageType.CARDHOLDER_AUTH_RP;
				break;
			case "0411":
				type = MessageType.AUTHORIZATION_RP_CRYPTO;
				break;
			case "0500":
				type = MessageType.TRANSCATION_VAL_NOTIF;
				break;
			case "0511":
				type = MessageType.TRANSACTION_VAL_ACK;
				break;
			default:
				break;
		}
		return type;
	}

	/**
	 * Permet de convertir !(le code message vers le type du message). Ex : 0101
	 * <- SECURE_CHANNEL_RQ
	 * 
	 * @param head
	 * @return
	 */
	public static String convertType2CodeMsg(MessageType type) {
		switch (type) {
			case SECURE_CHANNEL_RQ:
				return "0101";
			case SECURE_CHANNEL_RP:
				return "0110";
			case CARDHOLDER_AUTH_RQ:
				return "0301";
			case CARDHOLDER_AUTH_RP:
				return "0310";
			case AUTHORIZATION_RP_CRYPTO:
				return "0411";
			case TRANSCATION_VAL_NOTIF:
				return "0500";
			case TRANSACTION_VAL_ACK:
				return "0511";
			case UNKNOWN_TYPE:
				return null;
		}
		return null;
	}

	/**
	 * Transform string to ISOMsg
	 * 
	 * @param data
	 * @return
	 * @throws ISO7816Exception
	 */
	public static ISOMsg read(String data) throws ISO7816Exception {
		ISOMsg rp = new ISOMsg();
		rp.setPackager(getPackager());
		try {
			rp.unpack(data.getBytes());
		}
		catch (ISOException e) {
			throw new ISO7816Exception(e);
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

	// HashMap<String, String> ret = new CaseInsensitiveMap();
	// // head
	// String head = data.substring(0, 4);
	// ret.put("type", convertCodeMsg2Type(head).name());
	// int nbFields = 0;
	// try {
	// nbFields = Integer.parseInt(data.substring(4, 7));
	// ret.put("nbfields", "" + nbFields);
	// }
	// catch (Exception e) {
	// throw new ISO7816Exception("Interpretation data problem", e);
	// }
	//
	// // tag : 16 octets
	// // len : 3 octets
	// String tag = null;
	// int len = 0;
	// String value = null;
	// int c_index = 7;
	// while (c_index < data.length()) {
	// tag = data.substring(c_index, c_index + 16);
	// // remove 0 left padding
	// tag = tag.replaceFirst("^0+(?!$)", "");
	//
	// c_index += 16;
	//
	// try {
	// len = Integer.parseInt(data.substring(c_index, c_index + 3));
	// c_index += 3;
	// }
	// catch (Exception e) {
	// throw new ISO7816Exception("Interpretation data problem", e);
	// }
	//
	// value = data.substring(c_index, c_index + len);
	// c_index += len;
	//
	// ret.put(tag, value);
	// }
	//
	// // petite verif nbFields=hashmap.size-2
	// if (nbFields != ret.size() - 2) {
	// throw new
	// ISO7816Exception("Interpretation data problem, nbfields != fields parse");
	// }
	//
	// return ret;
	// }
}
