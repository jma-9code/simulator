package fr.ensicaen.simulator_ep.ep.strategies.fo.issuer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.utils.CommonNames;

public class FOIssuerStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOIssuerStrategy() {
		super();
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficeIssuer, Mediator m, String data) {

		// get chipset component reference
		ComponentIO issuerAuthorization = frontOfficeIssuer.getChild(CommonNames.FO_ISSUER_AUTH, ComponentIO.class);

		// get mediator between the issuer and the authorization module
		Mediator m_issuer_authorization = MediatorFactory.getInstance().getForwardMediator(m, issuerAuthorization);

		// forward to the chipset
		return m_issuer_authorization.send(frontOfficeIssuer, data);

	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FO/Issuer";
	}

}
