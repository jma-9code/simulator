package ep.strategies.ept;

import java.util.HashMap;

import model.component.ComponentIO;
import model.component.ComponentO;
import model.mediator.Mediator;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Utils;

public class EPTChipsetStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTChipsetStrategy.class);
	
	public void processEvent(ComponentO _this, String event) {
		switch(event) {
		case "CARD_INSERTED":
			// setting secure channel with the card
			
			// get the card
			
			
			break;
			
		default:
			log.info("Event "+event+" not implemented.");
		}
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator c, String data) {
		
		
		
		HashMap<String, String> d = Utils.string2Hashmap(data);
		

		//tpe.output(m, "content-type:iso7816;type:rq;msg:initco;protocols:B0',CB2A;ciphersetting:none,RSA2048")
		
		switch(d.get("msg")){
			case "initco":
				//c.send(tpe,"content-type:iso7816;type:rq;msg:initco;protocols:B0',CB2A;ciphersetting:none,RSA2048");
				break;	
			case "pin":
				
				break;
			case "arpc":
				
				break;		
		}
		
		return VoidResponse.build();
	} 

}
