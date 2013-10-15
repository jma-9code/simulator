package ep.strategies.card;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.component.Component;
import model.component.ComponentIO;
import model.mediator.Mediator;
import model.response.IResponse;
import model.response.DataResponse;
import model.strategies.IStrategy;
import utils.ISO7816Exception;
import utils.ISO7816Tools;
import utils.ISO7816Tools.MessageType;

public class ChipStrategy implements IStrategy<ComponentIO> {


	private static Logger log = LoggerFactory.getLogger(ChipStrategy.class);
	
	public ChipStrategy() {
		
	}
	
	@Override
	public IResponse processMessage(ComponentIO chip, Mediator m, String data){
		//la carte ne comprend que du 7816
		HashMap<String, String> sdata = null;;
		try {
			sdata = ISO7816Tools.read(data);
		} catch (ISO7816Exception e) {
			log.warn("Get unreadable msg", e);
			return DataResponse.build(m, "UNREADABLE MSG");
		}
		MessageType type = MessageType.valueOf(sdata.get("type"));
		switch(type){
			case SECURE_CHANNEL_RQ:
				log.debug("SC RQ" + sdata);
				return manageSC_RQ((ComponentIO)chip, m, sdata);
			case CARDHOLDER_AUTH_RQ:
				log.debug("AUTH RQ" + sdata);
				return manageAUTH_RQ((ComponentIO)chip, m, sdata);
			case AUTHORISATION_RP_CRYPTO:
				return manageARPC_RQ((ComponentIO)chip, m, sdata);
			case UNKNOWN_TYPE:
				break;
		}
		
		return DataResponse.build(m, "");
	}
	
	/**
	 * Permet de gerer la validation de transaction
	 * @param chip
	 * @param m
	 * @param data
	 */
	private IResponse manageARPC_RQ(ComponentIO chip, Mediator m, HashMap<String, String> data){		
		String posID = data.get(ISO7816Tools.FIELD_POSID);
		String opcode = data.get(ISO7816Tools.FIELD_OPCODE);
		String amount = data.get(ISO7816Tools.FIELD_AMOUNT);
		String apcode_tpe = data.get(ISO7816Tools.FIELD_APPROVALCODE);
		String rpcode = data.get(ISO7816Tools.FIELD_RESPONSECODE);
		String pan = chip.getProperties().get("pan");
		String apcode_cb = chip.getProperties().get("approvalcode");
		
		//Construction de la rp
		StringBuffer sb = new StringBuffer();
		//head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.TRANSCATION_VAL_NOTIF));
		sb.append("007");
		
		//data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, posID));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_OPCODE, opcode));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_AMOUNT, amount));
		if (apcode_tpe.equalsIgnoreCase(apcode_cb)){
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_APPROVALCODE, apcode_cb));
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_RESPONSECODE, rpcode));
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PAN, pan));
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));
		}else{
			log.debug("chip can't verify the approval code : " + apcode_tpe );
			return DataResponse.build(m, "");
		}

		return DataResponse.build(m, sb.toString());
	}
	
	/**
	 * Permet de gerer la requete d'authentification, debut de la transaction
	 * @param chip
	 * @param m
	 * @param data
	 */
	private IResponse manageAUTH_RQ(ComponentIO chip, Mediator m,
			HashMap<String, String> data) {
		String posID = data.get(ISO7816Tools.FIELD_POSID);
		String opcode = data.get(ISO7816Tools.FIELD_OPCODE);
		String amount = data.get(ISO7816Tools.FIELD_AMOUNT);
		String pinData = data.get(ISO7816Tools.FIELD_PINDATA);
		String pan = chip.getProperties().get("pan");
		String protocol = chip.getProperties().get(ISO7816Tools.FIELD_POSID+"-"+posID);
		//TODO verif prot != null, sinn erreur
		
		
		//Construction de la rp
		StringBuffer sb = new StringBuffer();
		//head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RP));
		sb.append("007");
		//data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, posID));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_OPCODE, opcode));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_AMOUNT, amount));
		//test plafond de la carte
		if (ISO7816Tools.readAMOUNT(amount) > Integer.parseInt(chip.getProperties().get("ceil"))){
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_CARDAGREEMENT, "0"));
		}else{
			sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_CARDAGREEMENT, "1"));
		}
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PINVERIFICATION, "1"));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PAN, pan));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));
		
		return DataResponse.build(m, sb.toString());
	}

	/**
	 * Permet de repondre a une requete de SC
	 * @param m
	 * @param data
	 */
	private IResponse manageSC_RQ(ComponentIO chip, Mediator m, HashMap<String, String> data){
		String protocol_choice = null;
		List<String> protocols = Arrays.asList(chip.getProperties().get("protocol").split(" "));
		String pref_protocol_tpe = data.get(ISO7816Tools.FIELD_PROTOCOLPREFERRED);
		String posID = data.get(ISO7816Tools.FIELD_POSID);
		
		//la carte gere le protocol voulu par le tpe
		if (protocols.contains(pref_protocol_tpe)){
			protocol_choice = pref_protocol_tpe;
		}else{
			//la carte prend un protocol en commun
			List<String> protocols_tpe = Arrays.asList(data.get(ISO7816Tools.FIELD_PROTOCOLLIST));
			boolean possible = protocols.retainAll(protocols_tpe);
			if (possible){
				//prend la premiere intersection
				protocol_choice = protocols.get(0);
			}else{
				log.debug("chip can't communicate with TPE protocols : " + protocols_tpe );
				return DataResponse.build(m, "");
			}
		}
		
		//stockage du protocol utilise avec le commercant
		chip.getProperties().put(ISO7816Tools.FIELD_POSID+ "_"+posID, protocol_choice);
		
		//Construction de la rp
		StringBuffer sb = new StringBuffer();
		//head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RP));
		sb.append("003");
		//data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, posID));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOL, protocol_choice));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));
		
		return DataResponse.build(m, sb.toString());
	}

}
