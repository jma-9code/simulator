package utils;

import java.util.HashMap;

import tools.CaseInsensitiveMap;
import utils.ISO7816Tools.MessageType;

public class ISO8583Tools {

	
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
		HashMap<String, String> ret = new CaseInsensitiveMap();
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
