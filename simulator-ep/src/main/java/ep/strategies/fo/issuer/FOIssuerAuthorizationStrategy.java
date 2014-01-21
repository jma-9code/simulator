package ep.strategies.fo.issuer;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ep.strategies.fo.FOStrategy;

import simulator.Context;
import utils.ISO8583Tools;

public class FOIssuerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficeIssuer, Mediator m, String data) {
		ISOMsg authorizationAnswer = new ISOMsg();
		authorizationAnswer.setPackager(ISO8583Tools.getPackager());
		try {
			authorizationAnswer.unpack(data.getBytes());
			authorizationAnswer.setMTI("0110");
			authorizationAnswer.set(7, "0810172400"); // date : MMDDhhmmss
			authorizationAnswer.set(39, "00");
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
		
		try {
			return DataResponse.build(m, new String(authorizationAnswer.pack()));
		}
		catch (ISOException e) {
			e.printStackTrace();
			return VoidResponse.build();
		} 
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FOIssuer";
	}

}
