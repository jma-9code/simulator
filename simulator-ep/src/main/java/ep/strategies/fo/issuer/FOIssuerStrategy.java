package ep.strategies.fo.issuer;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.mediator.Mediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ep.strategies.fo.FOStrategy;

import simulator.Context;

public class FOIssuerStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficeIssuer, Mediator m, String data) {
		
		// get chipset component reference
		ComponentIO issuerAuthorization = frontOfficeIssuer.getChild("Authorization", ComponentIO.class);

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
		return "FOIssuer";
	}

}
