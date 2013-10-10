package ep.strategies.card;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;
import simulator.Utils;
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
import model.strategies.IStrategy;

public class CardStrategy implements IStrategy {

	private static Logger log = LoggerFactory.getLogger(CardStrategy.class);


	@Override
	public void process(Component card, Mediator m, String data) {
		//tout les traitements de donnees sont gerees par la puce
		ComponentIO chip = null;
		ComponentO magstrippe = null;
		for (Component c : card.getComponents()){
			//seul le composant chip est IO, la piste est consideree en lecture seule
			if (c instanceof ComponentIO){
				chip = (ComponentIO) c;
			}
			if (c instanceof ComponentO){
				magstrippe = (ComponentO) c;
			}
		}
		
		Mediator m_card_chip = MediatorFactory.getInstance().getMediator((ComponentIO)card, chip, EMediator.HALFDUPLEX);
		chip.input(m_card_chip, data);
		
	}
	
}