package ep.strategies.card;

import java.util.HashMap;

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
	
	private ComponentIO chip;
	
	public ChipStrategy(ComponentIO _chip) {
		chip = _chip;
	}
	
	@Override
	public void process(Component component, Mediator mediator, String data) {
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
				manageSC_RQ(sdata);
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

	private void manageSC_RQ(HashMap<String, String> data){
		log.debug("receiv - SC RQ" + data);
	}

}
