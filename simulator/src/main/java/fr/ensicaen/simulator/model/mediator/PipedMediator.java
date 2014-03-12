package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.properties.PropertiesPlus;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Simulator;
import fr.ensicaen.simulator.tools.LogUtils;

public class PipedMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(PipedMediator.class);

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
			l.onSendData(c, data);
		}

		try {
			Simulator.barrier.await();
		}
		catch (BrokenBarrierException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(LogUtils.MARKER_MEDIATOR_MSG,
				c.getName() + " send " + data + " to " + ((c.equals(sender)) ? receiver.getName() : sender.getName()));

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

	public PropertiesPlus getProperties() {
		PropertiesPlus allprop = new PropertiesPlus();
		allprop.putAll(m1.getProperties());
		allprop.putAll(m2.getProperties());
		return allprop;
	}
}
