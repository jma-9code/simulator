package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Simulator;

public class ReverseHalfDuplexMediator extends Mediator {

	public ReverseHalfDuplexMediator() {
		super(null, null);
	}

	public ReverseHalfDuplexMediator(HalfDuplexMediator mediator) {
		super((IOutput) mediator.getReceiver(), (IInput) mediator.getSender());
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

	@Override
	public String toString() {
		return "M[ReverseHalfDuplex - " + this.sender + " <--> " + this.receiver + "]";
	}

}
