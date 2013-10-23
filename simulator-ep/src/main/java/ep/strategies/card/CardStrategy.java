package ep.strategies.card;

import model.component.ComponentIO;
import model.component.ComponentO;
import model.factory.MediatorFactory;
import model.mediator.Mediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;

public class CardStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(CardStrategy.class);

	@Override
	public void init(Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO card, Mediator m, String data) {
		// tout les traitements de donnees sont gerees par la puce
		ComponentIO chip = card.getChild("chip", ComponentIO.class);
		ComponentO magstrippe = card.getChild("magstrippe", ComponentO.class);

		// get mediator between chip and card
		Mediator m_card_chip = MediatorFactory.getInstance().getForwardMediator(m, chip);

		// forward to the chip
		return m_card_chip.send(card, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
		// TODO Auto-generated method stub

	}

}
