package ep.strategies.fo.purchaser;

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

public class FOPurchaserStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficePurchaser, Mediator m, String data) {
		
		// get chipset component reference
		ComponentIO purchaserAuthorization = frontOfficePurchaser.getChild("Authorization", ComponentIO.class);

		// get mediator between the issuer and the authorization module
		Mediator m_purchaser_authorization = MediatorFactory.getInstance().getForwardMediator(m, purchaserAuthorization);

		// forward to the chipset
		return m_purchaser_authorization.send(frontOfficePurchaser, data);
		
	
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FOIssuer";
	}

}
