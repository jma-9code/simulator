package ep.strategies.ept;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import model.component.Component;
import model.component.ComponentIO;
import model.component.ComponentO;
import model.mediator.HalfDuplexMediator;
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
	
	
	@Override
	public void processEvent(ComponentIO _this, String event) {
		switch(event) {
			case "CARD_INSERTED":
			// setting secure channel with the card
			// prepare initialization message
			String msg = prepareSecureChannelRQ(_this);

			// get the card linked
			Mediator m = new HalfDuplexMediator(null, null); // incorrect
			DataResponse res = (DataResponse) m.send(_this, msg);

			try {
				Map<String, String> parsedData = ISO7816Tools.read(res.getData());
				parsedData.put("current_protocol", parsedData.get(ISO7816Tools.FIELD_PROTOCOL));
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

	private String prepareSecureChannelRQ(Component _this) {
		StringBuilder sb = new StringBuilder();

		// head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RQ));
		sb.append("004");

		// data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, _this.getProperty("pos_id")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOLLIST, _this.getProperty("protocol_list")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PROTOCOLPREFERRED,
				_this.getProperty("protocol_prefered")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME,
				ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));

		return sb.toString();
	}
	
	private String prepareCardHolderAuthRQ(Component _this) {
		StringBuilder sb = new StringBuilder();

		// head
		sb.append(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RQ));
		sb.append("004");

		// data
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_POSID, _this.getProperty("pos_id")));
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_OPCODE, "00")); // 00=purchase
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_AMOUNT,
				"8052")); // 2 last figure for decimals
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_PINDATA,
				"1234")); // normally ciphered
		sb.append(ISO7816Tools.createformatTLV(ISO7816Tools.FIELD_DATETIME,
				ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())));

		return sb.toString();
	}

	

}
