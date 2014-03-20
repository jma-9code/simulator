package fr.ensicaen.simulator.model.mediator.implicit;

import fr.ensicaen.simulator.model.component.IInputOutput;
import fr.ensicaen.simulator.model.mediator.explicit.HalfDuplexMediator;

public class ChildHalfDuplexMediator extends HalfDuplexMediator {

	public ChildHalfDuplexMediator() {
		super();
	}

	public ChildHalfDuplexMediator(IInputOutput a, IInputOutput b) {
		super(a, b);

	}

}
