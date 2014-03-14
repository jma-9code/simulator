package fr.ensicaen.simulator_ep.ep.strategies.fo;

import java.util.ArrayList;
import java.util.List;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.tools.LogUtils;
import fr.ensicaen.simulator_ep.utils.ComponentEP;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator m, String data) {
		ISOMsg message8583 = null;
		Component composantCible = null;
		// faire le lien avec le message8583

		/* Si c'est une demande d'autorisation ... */
		try {
			message8583 = ISO8583Tools.read(data);
			log.info("MTI " + message8583.getMTI());
			switch (message8583.getMTI()) {
				case "0100":
					log.debug(LogUtils.MARKER_COMPONENT_INFO, "FO forward the msg to the acquirer module");
					composantCible = Component.getFirstChildType(_this, ComponentEP.FO_ACQUIRER.ordinal());
					break;

				default:

					break;
			}
		}
		catch (ISO8583Exception | ISOException e) {
			e.printStackTrace();
		}

		Mediator mForward = MediatorFactory.getInstance().getForwardMediator(m, (IInput) composantCible);

		// forward to the chipset
		return mForward.send(_this, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	@Override
	public String toString() {
		return "FO";
	}

}
