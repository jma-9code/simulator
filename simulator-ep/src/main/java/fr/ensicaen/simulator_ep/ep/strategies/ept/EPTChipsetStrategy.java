package fr.ensicaen.simulator_ep.ep.strategies.ept;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator.tools.LogUtils;
import fr.ensicaen.simulator_ep.utils.CB2AValues;
import fr.ensicaen.simulator_ep.utils.ComponentEP;
import fr.ensicaen.simulator_ep.utils.ISO7816Exception;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools.MessageType;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;
import fr.ensicaen.simulator_ep.utils.ProtocolEP;

public class EPTChipsetStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTChipsetStrategy.class);

	public static final String CKEY_ACQUIRER_ID = "acquirer_id";
	public static final String CKEY_ACCEPTOR_TERMINAL_ID = "acceptor_terminal_id";
	public static final String CKEY_ACCEPTOR_ID = "acceptor_id";
	public static final String CKEY_MERCHANT_CATEGORY_CODE = "merchant_category_code";
	public static final String CKEY_CURRENCY_CODE = "currency_code";
	public static final String CKEY_NB_MESSAGE = "nb_message";
	public static final String CKEYPREFIX_MESSAGE = "message_";

	public EPTChipsetStrategy() {
		super();
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		ArrayList<PropertyDefinition> defs = new ArrayList<PropertyDefinition>();
		defs.add(new PropertyDefinition(CKEY_ACQUIRER_ID, "", true, "Identifiant de l'acquéreur"));
		defs.add(new PropertyDefinition(CKEY_ACCEPTOR_TERMINAL_ID, "", true, "Identifiant du système d'acceptation"));
		defs.add(new PropertyDefinition(CKEY_ACCEPTOR_TERMINAL_ID, "", true, "Identifiant de l'accepteur"));
		defs.add(new PropertyDefinition(CKEY_MERCHANT_CATEGORY_CODE, "", false, "Code de catégorie de marchandise"));
		defs.add(new PropertyDefinition(CKEY_CURRENCY_CODE, "", true, "Code de la devise utilisée"));
		return defs;
	}

	@Override
	public void init(IOutput _this, Context ctx) {
		// enregistrement aux évènements suivants
		ctx.subscribeEvent(_this, "SMART_CARD_INSERTED");
		ctx.subscribeEvent(_this, "REMOTE_DATA_COLLECTION");

		((ComponentIO) _this).getProperties().put("pin_enter", null, true);
		((ComponentIO) _this).getProperties().put("amount", null, true);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
		// msg
		ISOMsg msg = null;

		switch (event) {

			case "SMART_CARD_INSERTED":

				try {
					log.info(LogUtils.MARKER_COMPONENT_INFO, "A card has been inserted in the EPT");
					msg = prepareSecureChannelRQ(_this);
					Mediator m = Context.getInstance().getFirstMediator(_this, ComponentEP.CARD.ordinal());
					m.setProtocol(ProtocolEP.ISO7816.toString());

					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends a request for secured channel");
					DataResponse res = (DataResponse) m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receive the response for secure channel");
					ISOMsg sdata = ISO7816Tools.read(res.getData());

					// card holder authentication (amount + PIN)
					msg = prepareCardHolderAuthRQ(_this, sdata);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends a authentification holder request");
					res = (DataResponse) m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receive the response for authentification holder");
					sdata = ISO7816Tools.read(res.getData());

					// verification du pin/ceil OK ?
					boolean card_OK = true;
					if (Integer.parseInt(sdata.getString(ISO7816Tools.FIELD_PINVERIFICATION)) != 1) {
						log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT PIN check error");
						card_OK = false;
					}
					if (Integer.parseInt(sdata.getString(ISO7816Tools.FIELD_CARDAGREEMENT)) != 1) {
						log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT exceeding the ceiling");
						card_OK = false;
					}
					if (!card_OK) {
						msg = prepareCheckFail(_this, sdata);
						m.send(_this, new String(msg.pack()));
					}
					else {
						// auth request to bank (TPE -> Bank and bank -> TPE)
						boolean fo_connection = false;
						try {
							log.info(LogUtils.MARKER_COMPONENT_INFO,
									"EPT try to join the FO, and send an authorization...");
							Mediator mFrontOffice = Context.getInstance().getFirstMediator(_this,
									ComponentEP.FRONT_OFFICE.ordinal());
							mFrontOffice.setProtocol(ProtocolEP.ISO8583.toString());
							msg = generateAuthorizationRequest(_this, sdata);
							res = (DataResponse) mFrontOffice.send(_this, new String(msg.pack()));
							log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receive authorization from the FO");
							sdata = ISO8583Tools.read(res.getData());
							fo_connection = !sdata.getValue(39).equals(CB2AValues.Field39.UNREACHABLE_CARD_ISSUER)
									&& !sdata.getValue(39).equals(CB2AValues.Field39.UNKNOWN_CARD_ISSUER);
						}
						catch (ContextException e) {
							log.warn(LogUtils.MARKER_COMPONENT_INFO, "EPT has not succeeded to reach the FO", e);
						}

						// ARPC
						// no connection with fo ? use previous msg
						msg = prepareARPC(_this, sdata, fo_connection);
						log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT send ARPC to the card");
						res = (DataResponse) m.send(_this, new String(msg.pack()));
						log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receive the final agreement");
						sdata = ISO7816Tools.read(res.getData());

						// final agreement
						manageFinalAgrement(_this, sdata);
					}
				}
				catch (ContextException e) {
					log.error("Context error", e);
					return; // ABORT (to think)
				}
				catch (ISO7816Exception e) {
					log.error("Get unreadable message from card", e);
					return; // ABORT
				}
				catch (ISOException e) {
					e.printStackTrace();
				}
				catch (ISO8583Exception e) {
					e.printStackTrace();
				}

				break;

			case "REMOTE_DATA_COLLECTION":

				try {
					ISOMsg resData;
					IResponse res;

					// recuperation du mediateur avec le module fo de
					// telecollecte
					Mediator m = Context.getInstance().getFirstMediator(_this,
							ComponentEP.FO_ACQUIRER_REMOTE_DATA_COLLECTION.ordinal());
					m.setProtocol(ProtocolEP.CB2A_TLC.toString());
					log.info(LogUtils.MARKER_COMPONENT_INFO, "Begin of remote data collection.");

					// ### 1 - initialisation de la télécollecte
					// sign-on (0804)
					msg = prepareSignOn(_this);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends sign-on message");
					res = m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives sign-on ack message");
					// res.throwExceptionIfVoid();

					// Response data : ACK (0814 attendu)
					resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
					if (!"0814".equals(resData.getMTI()) || !"0000".equals(resData.getValue(39))) {
						// On réagit ?
						log.error("0814 expected !!");
					}

					// init remise (0306)
					msg = prepareSubmissionHeader(_this);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends submission header message");
					res = m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives submission header ack message");
					// res.throwExceptionIfVoid();

					// Response data : ACK (0316 attendu)
					resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
					if (!"0316".equals(resData.getMTI()) || !"0000".equals(resData.getValue(39))) {
						// On réagit ?
						log.error("0316 expected !!");
					}

					// ### 2 - remise
					// Transaction financière (0246)
					int nbMessage = Integer.parseInt(_this.getProperty(CKEY_NB_MESSAGE));
					long totalAmount = 0;

					log.info(LogUtils.MARKER_COMPONENT_INFO, nbMessage + " to transfer");

					for (int i = 1; i <= nbMessage; i++) {
						msg = prepareFinancialTransaction(_this.getProperty(CKEYPREFIX_MESSAGE + nbMessage));
						log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends transaction " + i);
						res = m.send(_this, new String(msg.pack()));
						// res.throwExceptionIfVoid();

						// alimentation des totaux pour la consolidation
						totalAmount += Long.parseLong(msg.getString(4));

						if (!res.isVoid()) {
							// acquittement global ? (0256 attendu)
							resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());

							if ("0256".equals(resData.getMTI())) {
								log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives transfer ack message");
							}
						}
					}

					// ### 3 - fin de remise et consolidation
					msg = prepareFinancialConsolidation(_this, nbMessage, totalAmount);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends financial consolidation message");
					res = m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives financial consolidation ack message");
					// res.throwExceptionIfVoid();

					// Response data : ACK (0516 attendu)
					resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
					if (!"0516".equals(resData.getMTI())) {
						// On réagit ?
						log.error("0516 expected !!");
					}
					else {
						// Suppression des transactions
						_this.getProperties().put(CKEY_NB_MESSAGE, "0");
						_this.getProperties().removeKeyStartsWith(CKEYPREFIX_MESSAGE);
					}

					// ### 4 - fin de connexion
					// proposition de droit de parole
					msg = prepareRightToSpeak(_this);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends right to speak proposition message");
					res = m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives end of speak message");
					// res.throwExceptionIfVoid();

					// Response data : ACK (0854 attendu)
					resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
					if (!"0854".equals(resData.getMTI()) || !"1000".equals(resData.getValue(39))) {
						// On réagit ?
						log.error("0854 expected !!");
					}

					// notif de déconnexion
					msg = prepareDecoNotification(_this);
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT sends disconnect notification message");
					res = m.send(_this, new String(msg.pack()));
					log.info(LogUtils.MARKER_COMPONENT_INFO, "EPT receives disconnect notification message");
					// res.throwExceptionIfVoid();

					// Response data : ACK (0854 attendu)
					resData = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
					if (!"0854".equals(resData.getMTI()) || !"0000".equals(resData.getValue(39))) {
						// On réagit ?
						log.error("0854 expected !!");
					}

					log.info(LogUtils.MARKER_COMPONENT_INFO, "End of remote data collection.");

				}
				catch (ContextException e) {
					log.error("Context error", e);
					return; // ABORT (to think) } catch (ISO7816Exception e) {
				}
				catch (ISOException e) {
					e.printStackTrace();
				}
				catch (ISO8583Exception e) {
					e.printStackTrace();
				}
				catch (ISO7816Exception e) {
					e.printStackTrace();
				}

				break;

			default:
				log.warn(LogUtils.MARKER_COMPONENT_INFO, "Event " + event + " not implemented.");
		}
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator c, String data) {

		return VoidResponse.build();
	}

	/**
	 * Messae de connexion au front office (phase 1 - initialisation)
	 * 
	 * @param _this
	 * @return
	 */
	private ISOMsg prepareSignOn(Component _this) throws ISOException {
		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();

		msg.setMTI("0804"); // sign on
		// signOnMsg.set(11, "000009"); // STAN
		msg.set(12, ISO8583Tools.getDate_hhmmss()); // Heure hhmmss
		msg.set(13, ISO8583Tools.getDate_MMJJ()); // Date MMJJ
		msg.set(24, "862"); // Code Fonction => Dialogue + TLC
		msg.set(25, "10"); // Code Raison => "Programmé par Acquéreur"
		msg.set(32, _this.getProperty(CKEY_ACQUIRER_ID)); // (BIN+Banque)
		msg.set(41, _this.getProperty(CKEY_ACCEPTOR_TERMINAL_ID)); // id tpe
		msg.set(42, _this.getProperty(CKEY_ACCEPTOR_ID)); // id commercant
		msg.set(67, "01"); // Nb fichiers

		return msg;
	}

	/**
	 * Message d'initialisation de la remise (phase 1 - initialisation)
	 * 
	 * @param _this
	 * @return
	 */
	private ISOMsg prepareSubmissionHeader(Component _this) throws ISOException {
		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();

		msg.setMTI("0306"); // ENTETE FICHIER REMISE
		// signOnMsg.set(11, "000009"); // STAN
		msg.set(12, ISO8583Tools.getDate_hhmmss()); // Heure hhmmss
		msg.set(13, ISO8583Tools.getDate_MMJJ()); // Date MMJJ
		msg.set(18, _this.getProperty(CKEY_MERCHANT_CATEGORY_CODE));
		msg.set(26, "400001"); // Controle transfert
		msg.set(49, _this.getProperty(CKEY_CURRENCY_CODE));

		// n4 : identifiant du fichier
		// n4 : nombre de message
		// n2 : fenetre d'acquittement
		msg.set(70, "000001" + ISO8583Tools.paddingLeft(_this.getProperty(CKEY_NB_MESSAGE), 4, '0') + "99");

		return msg;
	}

	/**
	 * Message transportant une transaction financière (phase 2 - remise)
	 * 
	 * @param property
	 * @return
	 * @throws ISO8583Exception
	 * @throws ISO7816Exception
	 */
	private ISOMsg prepareFinancialTransaction(String data) throws ISOException, ISO8583Exception, ISO7816Exception {
		ISOMsg msgAuto = ISO7816Tools.read(data);

		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();
		msg.setMTI("0246"); // transaction financière
		msg.set(2, msgAuto.getString(3)); // pan
		msg.set(3, "000000"); // code traitement
		msg.set(4, msgAuto.getString(6)); // montant
		msg.set(12, msgAuto.getString(14).substring(4, 10)); // heure
		msg.set(13, msgAuto.getString(14).substring(0, 4)); // date
		msg.set(22, "000"); // condition de realisation
		msg.set(26, "000002"); // controle transfert
		// ignore stan (11), annee transaction (47 / 07), numero transaction (47
		// / 10)
		return msg;
	}

	/**
	 * Message de fin de remise et de consolidation financière (phase 3 - fin de
	 * remise)
	 * 
	 * @param _this
	 * @return
	 */
	private ISOMsg prepareFinancialConsolidation(ComponentIO _this, long nbCredit, long totalAmount)
			throws ISOException {
		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();

		msg.setMTI("0506"); // fin de remise
		msg.set(70, "00000200000199"); // Gestion du transfert
		// nb crédit
		msg.set(74, ISO8583Tools.paddingLeft(String.valueOf(nbCredit), 10, '0'));
		// nb débit
		msg.set(76, "0000000000");
		// nb débit annulées
		msg.set(77, "0000000000");
		// Montant total crédit
		msg.set(86, ISO8583Tools.paddingLeft(String.valueOf(totalAmount), 16, '0'));
		// Montant total débit
		msg.set(88, "0000000000000000");
		// Montant total débit annulées
		msg.set(89, "0000000000000000");

		return msg;
	}

	/**
	 * Message de proposition de droit de parole
	 * 
	 * @param _this
	 * @return
	 */
	private ISOMsg prepareRightToSpeak(ComponentIO _this) throws ISOException {
		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();

		msg.setMTI("0844"); // fin de remise
		msg.set(24, "851"); // Code fonction = proposition droit de parole

		return msg;
	}

	/**
	 * Message de notification de déconnexion
	 * 
	 * @param _this
	 * @return
	 */
	private ISOMsg prepareDecoNotification(ComponentIO _this) throws ISOException {
		ISOMsg msg = ISO8583Tools.create_CB2A_TLC();

		msg.setMTI("0844"); // fin de remise
		msg.set(24, "860"); // Code fonction = fermeture de dialogue

		return msg;
	}

	private ISOMsg prepareSecureChannelRQ(Component _this) throws ISOException {
		ISOMsg ret = ISO7816Tools.create();
		ret.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RQ));
		ret.set(ISO7816Tools.FIELD_POSID, _this.getProperties().get("pos_id"));
		ret.set(ISO7816Tools.FIELD_PROTOCOLLIST, _this.getProperties().get("protocol_list"));
		ret.set(ISO7816Tools.FIELD_PROTOCOLPREFERRED, _this.getProperties().get("protocol_prefered"));
		// ret.set(ISO7816Tools.FIELD_STAN, _this.getProperties().get("stan"));
		// ret.set(ISO7816Tools.FIELD_RRN, generateTransactid(_this));
		ret.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
		return ret;
	}

	/**
	 * Preparation de la transcation montant + PIN
	 * 
	 * @param _this
	 * @return
	 * @throws ISOException
	 */
	private ISOMsg prepareCardHolderAuthRQ(Component _this, ISOMsg data) throws ISOException {
		String pan = data.getString(ISO7816Tools.FIELD_STAN);
		ISOMsg ret = ISO7816Tools.create();
		ret.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RQ));
		ret.set(ISO7816Tools.FIELD_POSID, _this.getProperties().get("pos_id"));
		ret.set(ISO7816Tools.FIELD_OPCODE, "00");
		ret.set(ISO7816Tools.FIELD_AMOUNT,
				ISO7816Tools.writeAMOUNT(Float.parseFloat(_this.getProperties().get("amount"))));
		ret.set(ISO7816Tools.FIELD_PINDATA, _this.getProperties().get("pin_enter"));
		// ret.set(ISO7816Tools.FIELD_STAN, generateNextSTAN(_this, pan));
		// ret.set(ISO7816Tools.FIELD_RRN, generateTransactid(_this));
		ret.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
		return ret;
	}

	/**
	 * Preparation du msg d'autorisation pour le FO
	 * 
	 * @param _this
	 * @param parsedData
	 * @return
	 * @throws ISOException
	 */
	public ISOMsg generateAuthorizationRequest(ComponentIO _this, ISOMsg parsedData) throws ISOException {
		log.debug(LogUtils.MARKER_COMPONENT_INFO, "EPT sends a request for secured channel");
		String pan = parsedData.getString(ISO7816Tools.FIELD_PAN);
		String amount = parsedData.getString(ISO7816Tools.FIELD_AMOUNT);
		String stan = parsedData.getString(ISO7816Tools.FIELD_STAN);
		String apcode = parsedData.getString(ISO7816Tools.FIELD_APPROVALCODE);

		ISOMsg authorizationRequest = ISO8583Tools.create();

		authorizationRequest.setMTI("0100");
		authorizationRequest.set(2, pan); // PAN
		authorizationRequest.set(3, "000101"); // Type of Auth + accounts
		authorizationRequest.set(4, amount);
		authorizationRequest.set(7, ISO8583Tools.getDate_MMJJhhmmss());
		// authorizationRequest.set(11, generateNextSTAN(_this, stan));
		authorizationRequest.set(38, apcode);
		authorizationRequest.set(42, _this.getProperties().get("acceptor_id")); // Acceptor's
		// ID
		authorizationRequest.set(123, _this.getProperties().get("posdatacode"));

		return authorizationRequest;
	}

	/**
	 * Erreur lors de l'envoie de l'ARQC de la carte, le pin est faux || le
	 * plafond a ete depasse. Fin de transaction.
	 * 
	 * @param _this
	 * @param data
	 * @return
	 * @throws ISOException
	 */
	private ISOMsg prepareCheckFail(Component _this, ISOMsg data) throws ISOException {
		String amount = data.getString(4);
		String apcode = data.getString(38);
		String rescode = data.getString(39);
		String pan = data.getString(2);

		ISOMsg ret = ISO7816Tools.create();
		ret.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.AUTHORIZATION_RP_CRYPTO));
		ret.set(ISO7816Tools.FIELD_POSID, _this.getProperties().get("pos_id"));
		ret.set(ISO7816Tools.FIELD_OPCODE, "00");
		ret.set(ISO7816Tools.FIELD_AMOUNT, amount);
		ret.set(ISO7816Tools.FIELD_PAN, pan);

		// time out, no connection with the fo
		ret.set(ISO7816Tools.FIELD_RESPONSECODE, "69");
		ret.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
		return ret;
	}

	/**
	 * Preparation de l'arpc depuis les info donnees par le FO de la banque
	 * 
	 * @param _this
	 * @param data
	 *            msg in format 8583
	 * @return format 7816
	 * @throws ISOException
	 */
	private ISOMsg prepareARPC(Component _this, ISOMsg data, boolean fo_connection) throws ISOException {

		String amount = data.getString(4);
		String apcode = data.getString(38);
		String rescode = data.getString(39);
		String pan = data.getString(2);

		if (!fo_connection) {
			// use 7816 previous msg
			pan = data.getString(ISO7816Tools.FIELD_PAN);
			amount = data.getString(ISO7816Tools.FIELD_AMOUNT);
		}

		ISOMsg ret = ISO7816Tools.create();
		ret.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.AUTHORIZATION_RP_CRYPTO));
		ret.set(ISO7816Tools.FIELD_POSID, _this.getProperties().get("pos_id"));
		ret.set(ISO7816Tools.FIELD_OPCODE, "00");
		ret.set(ISO7816Tools.FIELD_AMOUNT, amount);
		ret.set(ISO7816Tools.FIELD_PAN, pan);
		// ret.set(ISO7816Tools.FIELD_STAN, generateNextSTAN(_this, stan));
		// ret.set(ISO7816Tools.FIELD_RRN, generateTransactid(_this));

		// response
		if (fo_connection) {
			ret.set(ISO7816Tools.FIELD_RESPONSECODE, rescode);
			ret.set(ISO7816Tools.FIELD_APPROVALCODE, apcode);
		}
		else {
			// time out, no connection with the fo
			ret.set(ISO7816Tools.FIELD_RESPONSECODE, "68");
		}
		ret.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));

		return ret;
	}

	/**
	 * Permet de gerer la reponse de la carte lors de la fin d'une transaction :
	 * le final agreement Cette reponse de la carte indique au TPE de stocker
	 * les donnees de la transaction pour la telecollecte
	 * 
	 * @param _this
	 * @param data
	 */
	private void manageFinalAgrement(Component _this, ISOMsg data) {
		// stockage de la transaction
		int nb_message = 0;

		try {
			nb_message = Integer.parseInt(_this.getProperty(CKEY_NB_MESSAGE));
		}
		catch (NumberFormatException e) {
		}

		// +1
		nb_message++;

		// sauvegarde
		try {
			_this.getProperties().put(CKEYPREFIX_MESSAGE + nb_message, new String(data.pack()));
			_this.getProperties().put(CKEY_NB_MESSAGE, String.valueOf(nb_message));
		}
		catch (ISOException e) {
			log.error("Error while saving transaction.");
		}

	}

	/**
	 * Permet de donner le numero de stan suivant. Le STAN ce code sur 6 digits
	 * !! met a jour les proprietes du chipset. !!
	 * 
	 * @param stan
	 *            courant
	 * @return
	 */
	public static String generateNextSTAN(Component _this, String curStan) {
		String ret = null;
		int val_stan = Integer.parseInt(curStan); // NumberFormatException non
													// gérée !!!
		if (val_stan > 999999) {
			ret = "000001";
		}
		else {
			ret = String.format("%06d", ++val_stan);
		}
		_this.getProperties().put("stan", ret);
		return ret;
	}

	/**
	 * Permet de generer un numero de transaction.
	 * 
	 * @param stan
	 * @return
	 */
	public static String generateTransactid(Component _this) {
		SimpleDateFormat sdf = new SimpleDateFormat("yDDDhh");
		return sdf.format(Context.getInstance().getTime()).substring(3) + _this.getProperties().get("stan");
	}

	@Override
	public String toString() {
		return "EPT/Chipset";
	}
}
