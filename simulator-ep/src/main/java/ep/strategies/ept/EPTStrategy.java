package ep.strategies.ept;

import model.component.ComponentIO;
import model.factory.MediatorFactory;
import model.mediator.Mediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EPTStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTStrategy.class);

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		// get chipset component reference
		ComponentIO chipset = _this.getChild("Chipset", ComponentIO.class);

		// get mediator between chipset and ept
		Mediator m_ept_chipset = MediatorFactory.getInstance().getForwardMediator(mediator, chipset);

		// forward to the chipset
		return m_ept_chipset.send(_this, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}
}
