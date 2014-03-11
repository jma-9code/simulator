package fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer;

import java.util.ArrayList;
import java.util.List;

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
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.utils.ComponentEP;

public class FOAcquirerStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOAcquirerStrategy() {
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
	public IResponse processMessage(ComponentIO frontOfficeAcquirer, Mediator m, String data) {

		// get chipset component reference
		Component purchaserAuthorization = Component.getFirstChildType(frontOfficeAcquirer,
				ComponentEP.FO_ACQUIRER_AUTHORIZATION.ordinal());

		// get mediator between the issuer and the authorization module
		Mediator m_purchaser_authorization = MediatorFactory.getInstance().getForwardMediator(m,
				(IInput) purchaserAuthorization);

		// forward to the chipset
		return m_purchaser_authorization.send(frontOfficeAcquirer, data);

	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FO/Acquirer";
	}

}
