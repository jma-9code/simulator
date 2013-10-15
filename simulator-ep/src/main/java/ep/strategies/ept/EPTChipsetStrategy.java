package ep.strategies.ept;

import java.util.Calendar;
import java.util.HashMap;

import model.component.ComponentIO;
import model.component.ComponentO;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ISO7816Exception;
import utils.ISO7816Tools;
import utils.ISO7816Tools.MessageType;


public class EPTChipsetStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTChipsetStrategy.class);
	
	public void processEvent(ComponentO _this, String event) {
		switch(event) {
			case "CARD_INSERTED":
			// setting secure channel with the card
			
			//get the card
			/*  TO CARD ISO76 RQ
			 *  03010050000000000POS.ID0100000623598000000000OP.CODE002000000000000A
				MOUNT010000000800000000000PIN.DATA004123400000000DATETIME010100817
				0934*/
				
			/*
			 * CARD RP 03100070000000000POS.ID0100000623598000000000OP.CODE002000000000000A
				MOUNT0100000008000PIN.VERIFICATION001100CARD.AGREEMENT0011000000000
				0000PAN016497671002564213000000000DATETIME0101008170936 
			 */
			
			// TO BANK ISO85
			
			break;
			
		default:
			log.info("Event "+event+" not implemented.");
		}
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator c, String data) {
		
		HashMap<String, String> sdata = null;;
		try {
			sdata = ISO7816Tools.read(data);
		} catch (ISO7816Exception e) {
			log.warn("Get unreadable msg", e);
			return DataResponse.build(c, "UNREADABLE MSG");
		}
		MessageType type = MessageType.valueOf(sdata.get("type"));
		switch(type){
			case SECURE_CHANNEL_RP:
				break;
			case CARDHOLDER_AUTH_RP:
				break;
			case TRANSCATION_VAL_NOTIF:
				break;
			case TRANSACTION_VAL_ACK:
				break;
			case UNKNOWN_TYPE:
				break;
		}
		
	
		
		return VoidResponse.build();
	}
	
	

}
