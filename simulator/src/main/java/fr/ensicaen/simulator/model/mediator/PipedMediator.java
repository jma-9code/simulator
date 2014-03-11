package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Simulator;

public class PipedMediator extends Mediator {

	private Mediator m1;
	private Mediator m2;

	public PipedMediator() {
		super(null, null);
	}

	public PipedMediator(Mediator m1, Mediator m2) {
		super(m1.getSender(), m2.getReceiver());
		this.m1 = m1;
		this.m2 = m2;
	}

	@Override
	public IResponse send(IOutput c, String data) {
		IResponse ret = null;
		for (MediatorListener l : listeners) {
			l.onSendData();
		}

		try {
			Simulator.barrier.await();
		}
		catch (BrokenBarrierException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (c == this.sender) {
			ret = this.receiver.notifyMessage(this, data);
		}
		else {
			ret = ((IInput) this.sender).notifyMessage(this, data);
		}

		Simulator.barrier.reset();

		return ret;
	}

	public Mediator getM1() {
		return m1;
	}

	public Mediator getM2() {
		return m2;
	}

}
