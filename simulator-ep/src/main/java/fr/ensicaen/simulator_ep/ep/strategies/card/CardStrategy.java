package fr.ensicaen.simulator_ep.ep.strategies.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;

public class CardStrategy implements IStrategy<ComponentIO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6678722786708638708L;
	private static Logger log = LoggerFactory.getLogger(CardStrategy.class);

	public CardStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO card, Mediator m, String data) {
		// tout les traitements de donnees sont gerees par la puce
		ComponentIO chip = card.getChild("chip", ComponentIO.class);
		// ComponentO magstrippe = card.getChild("magstrippe",
		// ComponentO.class);

		// get mediator between chip and card
		Mediator m_card_chip = MediatorFactory.getInstance().getForwardMediator(m, chip);

		// forward to the chip
		return m_card_chip.send(card, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Card";
	}
}
