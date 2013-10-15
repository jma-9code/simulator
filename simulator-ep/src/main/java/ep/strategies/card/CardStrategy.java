package ep.strategies.card;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;
import utils.ISO7816Exception;
import utils.ISO7816Tools;
import utils.ISO7816Tools.MessageType;
import model.component.Component;
import model.component.ComponentIO;
import model.component.ComponentO;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;
import model.response.IResponse;
import model.strategies.IStrategy;

public class CardStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(CardStrategy.class);
	
	@Override
	public IResponse processMessage(ComponentIO card, Mediator m, String data) {
		//tout les traitements de donnees sont gerees par la puce
		ComponentIO chip = card.getChild("chip", ComponentIO.class);
		ComponentO magstrippe =  card.getChild("magstrippe", ComponentO.class);		
		
		// get mediator between chip and card
		Mediator m_card_chip = MediatorFactory.getInstance().getForwardMediator(m, chip);

		
		// forward to the chip
		return m_card_chip.send(card, data);
	}
	
}
