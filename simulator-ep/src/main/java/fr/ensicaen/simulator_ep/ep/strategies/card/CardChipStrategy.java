package fr.ensicaen.simulator_ep.ep.strategies.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.tools.LogUtils;
import fr.ensicaen.simulator_ep.utils.ISO7816Exception;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools.MessageType;

public class CardChipStrategy implements IStrategy<ComponentIO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 350703633160515039L;
	private static Logger log = LoggerFactory.getLogger(CardChipStrategy.class);

	public enum State {
		OFF, SC, AUTH, VALID
	}

	public CardChipStrategy() {

	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
		ComponentIO c = (ComponentIO) _this;
		c.getProperties().put("state", State.OFF.toString());
		log.debug(LogUtils.MARKER_COMPONENT_INFO, "The card chip is reset");
	}

	@Override
	public IResponse processMessage(ComponentIO chip, Mediator m, String data) {
		IResponse ret = null;
		ISOMsg sdata;
		try {
			sdata = ISO7816Tools.read(data);
			State state = State.valueOf(chip.getProperty("state"));
			MessageType type = ISO7816Tools.convertCodeMsg2Type(sdata.getMTI());
			switch (state) {
				case OFF:
					if (type == MessageType.SECURE_CHANNEL_RQ) {
						log.info(LogUtils.MARKER_COMPONENT_INFO,
								"The card chip receive data to establish a secure channel with ETP");
						chip.getProperties().put("state", State.SC.name());
						ret = DataResponse.build(m, new String(manageSC_RQ(chip, sdata).pack()));
					}
					break;
				case SC:
					if (type == MessageType.CARDHOLDER_AUTH_RQ) {
						log.info(LogUtils.MARKER_COMPONENT_INFO,
								"The card chip receive data to verify identity of the holder");
						chip.getProperties().put("state", State.AUTH.name());
						ret = DataResponse.build(m, new String(manageAUTH_RQ(chip, sdata).pack()));
					}
					break;
				case AUTH:
					if (type == MessageType.AUTHORIZATION_RP_CRYPTO) {
						log.info(LogUtils.MARKER_COMPONENT_INFO, "The card chip receive ARPC data");
						chip.getProperties().put("state", State.OFF.name());
						ret = DataResponse.build(m, new String(manageARPC(chip, sdata).pack()));
					}
					break;
				default:
					throw new ISO7816Exception("Card state problem");
			}

			// chip.getProperties().put("state", State.OFF.name());

			// ret = DataResponse.build(m, "");

		}
		catch (ISO7816Exception | ISOException e) {
			log.warn("Get unreadable msg", e);
			ret = DataResponse.build(m, "UNREADABLE MSG");
		}
		return ret;
	}

	/**
	 * Permet de gerer la validation de transaction
	 * 
	 * @param chip
	 * @param m
	 * @param data
	 * @throws ISOException
	 */
	private ISOMsg manageARPC(ComponentIO chip, ISOMsg data) throws ISOException {
		// Construction de la rp
		ISOMsg rp = ISO7816Tools.create();
		String posID = data.getString(ISO7816Tools.FIELD_POSID);
		String opcode = data.getString(ISO7816Tools.FIELD_OPCODE);
		String amount = data.getString(ISO7816Tools.FIELD_AMOUNT);
		String apcode_tpe = data.getString(ISO7816Tools.FIELD_APPROVALCODE);
		String rpcode = data.getString(ISO7816Tools.FIELD_RESPONSECODE);
		// String transactID = data.getString(ISO7816Tools.FIELD_RRN);
		// String stan =
		// ISO7816Tools.generateSTAN(data.getString(ISO7816Tools.FIELD_STAN));
		String pan = chip.getProperties().get("pan");
		String apcode_cb = chip.getProperties().get("approvalcode");
		String datetime = ISO7816Tools.writeDATETIME(Context.getInstance().getTime());

		rp.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.TRANSCATION_VAL_NOTIF));
		rp.set(ISO7816Tools.FIELD_POSID, posID);
		rp.set(ISO7816Tools.FIELD_OPCODE, opcode);
		rp.set(ISO7816Tools.FIELD_AMOUNT, amount);
		rp.set(ISO7816Tools.FIELD_DATETIME, datetime);
		rp.set(ISO7816Tools.FIELD_PAN, pan);
		if (rpcode.equals("00") && apcode_tpe.equalsIgnoreCase(apcode_cb)) {
			rp.set(ISO7816Tools.FIELD_APPROVALCODE, apcode_cb);
			rp.set(ISO7816Tools.FIELD_RESPONSECODE, rpcode);
			chip.getProperties().put(datetime, rp.toString());
		}
		else {
			log.warn("chip can't verify the approval code or not connection with the FO (approvalcode from ept="
					+ apcode_tpe + ",rpcode=" + rpcode + ")");
		}

		return rp;
	}

	/**
	 * Permet de gerer la requete d'authentification, debut de la transaction
	 * 
	 * @param chip
	 * @param m
	 * @param data
	 * @throws ISOException
	 */
	private ISOMsg manageAUTH_RQ(ComponentIO chip, ISOMsg data) throws ISOException {
		ISOMsg rp = ISO7816Tools.create();
		String posID = data.getString(ISO7816Tools.FIELD_POSID);
		String opcode = data.getString(ISO7816Tools.FIELD_OPCODE);
		String amount = data.getString(ISO7816Tools.FIELD_AMOUNT);
		String pinData = data.getString(ISO7816Tools.FIELD_PINDATA);
		String pan = chip.getProperties().get("pan");
		String protocol = chip.getProperties().get(ISO7816Tools.FIELD_POSID + "-" + posID);
		// String transactID = data.getString(ISO7816Tools.FIELD_RRN);
		// String stan =
		// ISO7816Tools.generateSTAN(data.getString(ISO7816Tools.FIELD_STAN));
		// TODO verif prot != null, sinn erreur

		// Construction de la rp

		rp.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RP));
		rp.set(ISO7816Tools.FIELD_POSID, posID);
		rp.set(ISO7816Tools.FIELD_OPCODE, opcode);
		rp.set(ISO7816Tools.FIELD_AMOUNT, amount);

		// test plafond de la carte
		if (ISO7816Tools.readAMOUNT(amount) > Integer.parseInt(chip.getProperties().get("ceil"))) {
			rp.set(ISO7816Tools.FIELD_CARDAGREEMENT, "0");
		}
		else {
			rp.set(ISO7816Tools.FIELD_CARDAGREEMENT, "1");
		}
		if (pinData.equals(chip.getProperties().get("pin"))) {
			rp.set(ISO7816Tools.FIELD_PINVERIFICATION, "1");
		}
		else {
			rp.set(ISO7816Tools.FIELD_PINVERIFICATION, "0");
		}

		rp.set(ISO7816Tools.FIELD_PAN, pan);
		// rp.set(ISO7816Tools.FIELD_STAN, stan);
		// rp.set(ISO7816Tools.FIELD_RRN, transactID);
		rp.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
		return rp;

	}

	/**
	 * Permet de repondre a une requete de SC
	 * 
	 * @param m
	 * @param data
	 * @throws ISOException
	 */
	private ISOMsg manageSC_RQ(ComponentIO chip, ISOMsg data) throws ISOException {
		ISOMsg rp = ISO7816Tools.create();
		String protocol_choice = null;
		List<String> protocols = Arrays.asList(chip.getProperties().get("protocol").split(" "));
		String pref_protocol_tpe = data.getString(ISO7816Tools.FIELD_PROTOCOLPREFERRED);
		String posID = data.getString(ISO7816Tools.FIELD_POSID);
		String transactID = data.getString(ISO7816Tools.FIELD_RRN);
		// String stan =
		// ISO7816Tools.generateSTAN(data.getString(ISO7816Tools.FIELD_STAN));

		// la carte gere le protocol voulu par le tpe
		if (protocols.contains(pref_protocol_tpe)) {
			protocol_choice = pref_protocol_tpe;
		}
		else {
			// la carte prend un protocol en commun
			List<String> protocols_tpe = Arrays.asList(data.getString(ISO7816Tools.FIELD_PROTOCOLLIST));
			boolean possible = protocols.retainAll(protocols_tpe);
			if (possible) {
				// prend la premiere intersection
				protocol_choice = protocols.get(0);
			}
			else {
				log.debug("chip can't communicate with TPE protocols : " + protocols_tpe);
				return rp;
			}
		}

		// stockage du protocol utilise avec le commercant
		chip.getProperties().put("POSID_" + posID, protocol_choice);

		// Construction de la rp

		rp.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RP));
		rp.set(ISO7816Tools.FIELD_POSID, posID);
		rp.set(ISO7816Tools.FIELD_PROTOCOL, protocol_choice);
		// rp.set(ISO7816Tools.FIELD_STAN, stan);
		// rp.set(ISO7816Tools.FIELD_RRN, transactID);
		rp.set(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
		rp.set(ISO7816Tools.FIELD_POSID, posID);
		return rp;

	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "Card/Chip";
	}
}
