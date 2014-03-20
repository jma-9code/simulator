package fr.ensicaen.simulator.model.mediator.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInputOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.explicit.HalfDuplexMediator;
import fr.ensicaen.simulator.model.properties.PropertiesPlus;

public class ReverseHalfDuplexMediator extends HalfDuplexMediator {

	private static Logger log = LoggerFactory.getLogger(ReverseHalfDuplexMediator.class);

	private Mediator origine = null;

	public ReverseHalfDuplexMediator() {
	}

	public ReverseHalfDuplexMediator(HalfDuplexMediator mediator) {
		super((IInputOutput) mediator.getReceiver(), (IInputOutput) mediator.getSender());
		origine = mediator;
	}

	@Override
	public PropertiesPlus getProperties() {
		PropertiesPlus allprop = new PropertiesPlus();
		allprop.putAll(origine.getProperties());
		allprop.putAll(properties);
		return allprop;
	}
}
