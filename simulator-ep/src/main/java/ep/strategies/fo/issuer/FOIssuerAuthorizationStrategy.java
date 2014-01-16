package ep.strategies.fo.issuer;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.mediator.Mediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ep.strategies.fo.FOStrategy;

import simulator.Context;

public class FOIssuerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficeIssuer, Mediator m, String data) {
		ISOMsg message8583 = new ISOMsg();
		ComponentIO composantCible = null;
		Mediator mediateurAUtiliser;
		
		try {
			message8583.unpack(data.getBytes());
			message8583.setMTI("0110");
			message8583.set(7, "0810172400"); // date : MMDDhhmmss
			message8583.set(39, "00");
			m.send(frontOfficeIssuer, new String (message8583.pack()));
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
		
		
	
		return null;
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FOIssuer";
	}

}
