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
import model.strategies.IStrategy;
import utils.ISO7816Exception;
import utils.ISO7816Tools;
import utils.ISO7816Tools.MessageType;

public class ChipStrategy implements IStrategy{


	private static Logger log = LoggerFactory.getLogger(ChipStrategy.class);
	
	public ChipStrategy() {
		
	}
	
	@Override
	public void process(Component chip, Mediator m, String data){
		//la carte ne comprend que du 7816
		HashMap<String, String> sdata = null;;
		try {
			sdata = ISO7816Tools.data2Hash(data);
		} catch (ISO7816Exception e) {
			log.warn("Get unreadable msg", e);
			return;
		}
		MessageType type = MessageType.valueOf(sdata.get("type"));
		switch(type){
			case SECURE_CHANNEL_RQ:
				log.debug("SC RQ" + data);
				manageSC_RQ((ComponentIO)chip, m, sdata);
				break;
			case SECURE_CHANNEL_RP:
				break;
			case CARDHOLDER_AUTH_RQ:
				break;
			case CARDHOLDER_AUTH_RP:
				break;
			case AUTHORISATION_RP_CRYPTO:
				break;
			case TRANSCATION_VAL_NOTIF:
				break;
			case TRANSACTION_VAL_ACK:
				break;
			case UNKNOWN_TYPE:
				break;
		}
	}

	/**
	 * Permet de repondre a une requete de SC
	 * @param m
	 * @param data
	 */
	private void manageSC_RQ(ComponentIO chip, Mediator m, HashMap<String, String> data){
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
				return;
			}
		}
		
		//stockage du protocol utilise avec le commercant
		chip.getProperties().put(ISO7816Tools.FIELD_POSID+ "-"+posID, protocol_choice);
		
		//Construction de la rp
		StringBuffer sb = new StringBuffer();
		//head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RP));
		sb.append("003");
		//data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, posID));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOL, protocol_choice));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME, ISO7816Tools.formatDateTime(Calendar.getInstance().getTime())));
		m.send(chip, sb.toString());
	}

}
