package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ISO7816Tools {

	public enum MessageType {
		SECURE_CHANNEL_RQ, SECURE_CHANNEL_RP, CARDHOLDER_AUTH_RQ, CARDHOLDER_AUTH_RP, AUTHORIZATION_RP_CRYPTO, TRANSCATION_VAL_NOTIF, TRANSACTION_VAL_ACK, UNKNOWN_TYPE;
	}

	// champs utilises dans la 7816
	public static final String FIELD_POSID = "POS ID";
	public static final String FIELD_PROTOCOLLIST = "PROTOCOL LIST";
	public static final String FIELD_PROTOCOL = "PROTOCOL";
	public static final String FIELD_DATETIME = "DATETIME";
	public static final String FIELD_PROTOCOLPREFERRED = "PREFERRED";
	public static final String FIELD_AMOUNT = "AMOUNT";
	public static final String FIELD_OPCODE = "OP CODE";
	public static final String FIELD_PINDATA = "PIN DATA";
	public static final String FIELD_PINVERIFICATION = "PIN VERIFICATION";
	public static final String FIELD_CARDAGREEMENT = "CARD AGREEMENT";
	public static final String FIELD_PAN = "PAN";
	public static final String FIELD_APPROVALCODE = "APPROVAL CODE";
	public static final String FIELD_STAN = "STAN";//
	public static final String FIELD_RRN = "RET REF NUMB";// transcaction ID
	public static final String FIELD_RESPONSECODE = "RESPONSE CODE";

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
			ret = String.format("%d06", val_stan++);
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
		String r = String.format("%.2f%n", d);
		r = r.replace(",", "");
		return r;
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
	 * Permet de transformer le message normaliser vers une structure de donnees
	 * de type hashmap.
	 * 
	 * @param data
	 * @return
	 * @throws ISO7816Exception
	 */
	public static HashMap<String, String> read(String data) throws ISO7816Exception {
		HashMap<String, String> ret = new HashMap<>();
		// head
		String head = data.substring(0, 4);
		ret.put("type", convertCodeMsg2Type(head).name());
		int nbFields = 0;
		try {
			nbFields = Integer.parseInt(data.substring(4, 7));
			ret.put("nbfields", "" + nbFields);
		}
		catch (Exception e) {
			throw new ISO7816Exception("Interpretation data problem", e);
		}

		// tag : 16 octets
		// len : 3 octets
		String tag = null;
		int len = 0;
		String value = null;
		int c_index = 7;
		while (c_index < data.length()) {
			tag = data.substring(c_index, c_index + 16);
			// remove 0 left padding
			tag = tag.replaceFirst("^0+(?!$)", "");

			c_index += 16;

			try {
				len = Integer.parseInt(data.substring(c_index, c_index + 3));
				c_index += 3;
			}
			catch (Exception e) {
				throw new ISO7816Exception("Interpretation data problem", e);
			}

			value = data.substring(c_index, c_index + len);
			c_index += len;

			ret.put(tag, value);
		}

		// petite verif nbFields=hashmap.size-2
		if (nbFields != ret.size() - 2) {
			throw new ISO7816Exception("Interpretation data problem, nbfields != fields parse");
		}

		return ret;
	}
}
