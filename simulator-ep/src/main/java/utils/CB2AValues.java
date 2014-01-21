package utils;

public class CB2AValues {

	/** Field 39 : Response Code */
	public class Field39 {
		/**
		 * Use : Issuer server<br />
		 * "Transaction approuvée ou traitée avec succès"
		 */
		public final static String TRANSACTION_APPROVED = "00";
		/**
		 * Use : RCB Network or Issuer server<br />
		 * "Emetteur de cartes inconnu"
		 */
		public final static String UNKNOWN_CARD_ISSUER = "15";
		/**
		 * Use : RCB Network<br />
		 * "Réponse erronée"
		 */
		public final static String INVALID_RESPONSE = "20";
		/**
		 * Use : RCB Network or Issuer server<br />
		 * "Erreur de format"
		 */
		public final static String INVALID_FORMAT = "30";
		/**
		 * Use : RCB Network<br />
		 * "Identifiant de l’organisme acquéreur inconnu"
		 */
		public final static String UNKNOWN_ACQUIRER_ID = "31";
		/**
		 * Use : RCB Network or Issuer server<br />
		 * "Règles de sécurité non respectées"
		 */
		public final static String SECURITY_RULES_NOT_RESPECTED = "63";
		/**
		 * Use : RCB Network<br />
		 * "Emetteur de carte inaccessible"
		 */
		public final static String UNREACHABLE_CARD_ISSUER = "91";
		/**
		 * Use : RCB Network<br />
		 * "Echéance de la temporisation de surveillance globale"
		 */
		public final static String GLOBAL_TIMEOUT_EXCEEDED = "97";
	}
}
