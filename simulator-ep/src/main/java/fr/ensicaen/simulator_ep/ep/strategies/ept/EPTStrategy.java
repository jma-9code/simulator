package fr.ensicaen.simulator_ep.ep.strategies.ept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.utils.ComponentEP;

public class EPTStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(EPTStrategy.class);

	public EPTStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		// get chipset component reference
		Component chipset = Component.getFirstChildType(_this, ComponentEP.ETP_CHIPSET.ordinal());
		// get mediator between chipset and ept
		Mediator m_ept_chipset = MediatorFactory.getInstance().getForwardMediator(mediator, (IInput) chipset);

		// forward to the chipset
		return m_ept_chipset.send(_this, data);
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	@Override
	public String toString() {
		return "EPT";
	}
}
