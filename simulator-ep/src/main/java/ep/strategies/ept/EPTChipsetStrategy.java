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
		
		
		
	
		
		return VoidResponse.build();
	} 

}
