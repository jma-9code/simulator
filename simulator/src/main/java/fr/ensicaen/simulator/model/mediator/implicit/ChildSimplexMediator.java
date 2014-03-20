package fr.ensicaen.simulator.model.mediator.implicit;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.explicit.SimplexMediator;

public class ChildSimplexMediator extends SimplexMediator {

	public ChildSimplexMediator() {
		super();
	}

	public ChildSimplexMediator(IOutput a, IInput b) {
		super(a, b);
	}

}
