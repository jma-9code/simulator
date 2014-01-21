package fr.ensicaen.simulator_ep.ep.strategies.fo.issuer;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOIssuerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOIssuerAuthorizationStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

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
