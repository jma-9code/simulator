package fr.ensicaen.simulator.model.mediator;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;

public class ChildSimplexMediator extends SimplexMediator {

	public ChildSimplexMediator() {
		super();
	}

	public ChildSimplexMediator(IOutput a, IInput b) {
		super(a, b);
	}

}
