package fr.ensicaen.simulator_ep.ep.strategies.fo;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOffice, Mediator m, String data) {
		ISOMsg message8583 = null;
		ComponentIO composantCible = null;
		// faire le lien avec le message8583

		/* Si c'est une demande d'autorisation ... */
		try {
			message8583 = ISO8583Tools.read(data);
			log.info("MTI " + message8583.getMTI());
			switch (message8583.getMTI()) {
				case "0100":
					composantCible = frontOffice.getChild("Acquirer", ComponentIO.class);
					break;

				default:

					break;
			}
		}
		catch (ISO8583Exception | ISOException e) {
			e.printStackTrace();
		}

		Mediator mForward = MediatorFactory.getInstance().getForwardMediator(m, composantCible);

		// forward to the chipset
		return mForward.send(composantCible, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	@Override
	public String toString() {
		return "FO";
	}

}
