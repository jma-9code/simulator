package ep.strategies.ept;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import model.component.Component;
import model.component.ComponentIO;
import model.component.IOutput;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;
import simulator.exception.ContextException;
import tools.CaseInsensitiveMap;
import utils.ISO7816Exception;
import utils.ISO7816Tools;
import utils.ISO7816Tools.MessageType;

public class EPTChipsetStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTChipsetStrategy.class);

	@Override
	public void init(IOutput _this, Context ctx) {
		ctx.subscribeEvent(_this, "SMART_CARD_INSERTED");
	}

	public ISOMsg generateAuthorizationRequest(ComponentIO _this, Map<String, String> parsedData) {
		ISOMsg authorizationRequest = new ISOMsg();
		GenericPackager packager;
		try {
			packager = new GenericPackager(EPTChipsetStrategy.class.getResourceAsStream("8583.xml"));
			authorizationRequest.setPackager(packager);
			authorizationRequest.setMTI("0100");
			authorizationRequest.set(2, parsedData.get(ISO7816Tools.FIELD_PAN)); // PAN
			authorizationRequest.set(3, "000101"); // Type of Auth + accounts
			authorizationRequest.set(4, parsedData.get(ISO7816Tools.FIELD_AMOUNT)); // 100€
			authorizationRequest.set(7, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
																										// :
																										// MMDDhhmmss
			authorizationRequest.set(11, generateNextSTAN(_this, parsedData.get(ISO7816Tools.FIELD_STAN))); // System
																											// Trace
																											// Audit
																											// Number
			authorizationRequest.set(38, ISO7816Tools.FIELD_APPROVALCODE); // Approval
																			// Code
			authorizationRequest.set(42, "623598"); // Acceptor's ID
			authorizationRequest.set(123, ISO7816Tools.FIELD_POSID); // POS Data
																		// Code
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
		return authorizationRequest;
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
		switch (event) {
			case "SMART_CARD_INSERTED":
				// setting secure channel with the card
				// prepare initialization message
				String msg = prepareSecureChannelRQ(_this);

				// get the card linked
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Smart Card Reader");
					DataResponse res = (DataResponse) m.send(_this, msg);

					Map<String, String> parsedData = ISO7816Tools.read(res.getData());

					// card holder authentication (amount + PIN)
					msg = prepareCardHolderAuthRQ(_this, parsedData);
					res = (DataResponse) m.send(_this, msg);
					parsedData = ISO7816Tools.read(res.getData());

					// auth request to bank (TPE -> Bank and bank -> TPE)
					// Create ISO Message
					Mediator mediateurFrontOffice = Context.getInstance().getFirstMediator(_this, "FrontOffice");
					ISOMsg authorizationAnswer = null;
					try {
						authorizationAnswer = new ISOMsg();
						authorizationAnswer.unpack(((DataResponse) mediateurFrontOffice.send(_this, new String(
								generateAuthorizationRequest(_this, parsedData).pack()))).getData().getBytes());
					}
					catch (ISOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						if (authorizationAnswer.getValue(39) != "00") {
							log.error("Authorization rejected by the bank");
							return;
						}
					}
					catch (ISOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// ...
					Map<String, String> rpfromBank = new CaseInsensitiveMap();
					rpfromBank.put("approvalcode", "07B56=");
					rpfromBank.put("responsecode", "00");
					rpfromBank.put("pan", parsedData.get("PAN"));
					rpfromBank.put("stan", ISO7816Tools.generateSTAN(_this.getProperty("STAN")));
					rpfromBank.put("amount", parsedData.get("amount"));
					rpfromBank.put(ISO7816Tools.FIELD_RRN, generateTransactid(_this));

					// ARPC
					msg = prepareARPC(_this, rpfromBank);
					res = (DataResponse) m.send(_this, msg);
					parsedData = ISO7816Tools.read(res.getData());

					// final agreement
					manageFinalAgrement(_this, parsedData);

				}
				catch (ContextException e) {
					log.error("Context error", e);
					return; // ABORT (to think)
				}
				catch (ISO7816Exception e) {
					log.error("Get unreadable message from card", e);
					return; // ABORT
				}

				break;

			default:
				log.info("Event " + event + " not implemented.");
		}
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator c, String data) {

		return VoidResponse.build();
	}

	private String prepareSecureChannelRQ(Component _this) {
		StringBuilder sb = new StringBuilder();

		// head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RQ));
		sb.append("006");

		// data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, _this.getProperty("pos_id")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOLLIST, _this.getProperty("protocol_list")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOLPREFERRED,
				_this.getProperty("protocol_prefered")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_STAN, _this.getProperty("stan")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_RRN, generateTransactid(_this)));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME,
				ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));

		return sb.toString();
	}

	/**
	 * Preparation de la transcation montant + PIN
	 * 
	 * @param _this
	 * @return
	 */
	private String prepareCardHolderAuthRQ(Component _this, Map<String, String> data) {

		StringBuilder sb = new StringBuilder();

		// head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RQ));
		sb.append("007");

		// data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, _this.getProperty("pos_id")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_OPCODE, "00")); // 00=purchase
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_AMOUNT, ISO7816Tools.writeAMOUNT(80)));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PINDATA, "1234")); // normally
																						// ciphered
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_STAN,
				generateNextSTAN(_this, data.get(ISO7816Tools.FIELD_STAN))));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_RRN, generateTransactid(_this)));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME,
				ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));

		return sb.toString();
	}

	/**
	 * Preparation de la requete de demande d'auth a la banque du tpe. 8583
	 * 
	 * @param _this
	 * @return
	 */
	private String prepareBankAuthRQ(Component _this) {
		return null;
	}

	/**
	 * Preparation de l'arpc depuis les info donnees par le FO de la banque
	 * 
	 * @param _this
	 * @return
	 */
	private String prepareARPC(Component _this, Map<String, String> data) {
		/*
		 * 04110070000000000POS.ID0100000623598000000000OP.CODE002000000000000A
		 * MOUNT0100000008000000APPROVAL.CODE00607B56=000RESPONSE.CODE002000
		 * 000000000000PAN016497671002564213000000000DATETIME0101008173026
		 */
		StringBuilder sb = new StringBuilder();

		// head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.AUTHORIZATION_RP_CRYPTO));
		sb.append("009");

		// data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, _this.getProperty("pos_id")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_OPCODE, "00")); // 00=purchase
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_AMOUNT, data.get("amount")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_APPROVALCODE, data.get("approvalcode")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_RESPONSECODE, data.get("responsecode")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PAN, data.get(ISO7816Tools.FIELD_PAN)));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_STAN,
				generateNextSTAN(_this, data.get(ISO7816Tools.FIELD_STAN))));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_RRN, generateTransactid(_this)));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME,
				ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));

		return sb.toString();
	}

	/**
	 * Permet de gerer la reponse de la carte lors de la fin d'une transaction :
	 * le final agreement Cette reponse de la carte indique au TPE de stocker
	 * les donnees de la transaction pour la telecollecte
	 * 
	 * @param _this
	 * @param data
	 */
	private void manageFinalAgrement(Component _this, Map<String, String> data) {
		// stockage de la transaction
		String datetime = data.get(ISO7816Tools.FIELD_DATETIME);
		String stan = data.get(ISO7816Tools.FIELD_STAN);
		_this.getProperties().put(datetime + stan, data.toString());
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
		int val_stan = Integer.parseInt(curStan);
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
		SimpleDateFormat sdf = new SimpleDateFormat("yDhh");
		return sdf.format(Context.getInstance().getTime()) + _this.getProperty("stan");
	}

	@Override
	public String toString() {
		return "EPTChipset";
	}
}
