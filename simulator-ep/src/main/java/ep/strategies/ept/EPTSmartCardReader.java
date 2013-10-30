package ep.strategies.ept;

import model.component.ComponentIO;
import model.component.IOutput;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;
import simulator.exception.ContextException;

public class EPTSmartCardReader implements IStrategy<ComponentIO> {
	private static Logger log = LoggerFactory.getLogger(EPTSmartCardReader.class);

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		try {
			Mediator m = Context.getInstance().getFirstMediator(_this, "Smart Card");
			return m.send(_this, data);
		}
		catch (ContextException e) {
			log.error("Context error", e);
			return DataResponse.build(mediator, "ERROR"); // TODO Error code in
															// protocol ?
		}
	}
}
