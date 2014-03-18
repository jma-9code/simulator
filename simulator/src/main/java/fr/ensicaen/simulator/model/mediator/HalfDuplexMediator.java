package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IInputOutput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.Simulator;
import fr.ensicaen.simulator.tools.LogUtils;

/**
 * Permet d'envoyer un message dans un canal multi-directionnel entre deux
 * composants Rq : Utilisable uniquement entre deux composants IO
 * 
 * @author JM
 * 
 */
public class HalfDuplexMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(HalfDuplexMediator.class);

	public HalfDuplexMediator() {
	}

	public HalfDuplexMediator(IInputOutput a, IInputOutput b) {
		super(a, b);

		// auto register
		Context.getInstance().registerMediator(this, true);
	}

	@Override
	public IResponse send(IOutput c, String data) {
		IResponse ret = null;
		for (MediatorListener l : listeners) {
			l.onSendData(this, false, data);
		}

		try {
			Simulator.barrier.await();
		}
		catch (BrokenBarrierException | InterruptedException e) {
			// TODO Auto-generated catch block
			log.debug(LogUtils.MARKER_MEDIATOR_MSG, "broken barrier for mediator : " + this);
		}

		log.info(LogUtils.MARKER_MEDIATOR_MSG,
				c.getName() + " send " + data + " to " + ((c.equals(sender)) ? receiver.getName() : sender.getName()));

		if (c == this.sender) {
			ret = this.receiver.notifyMessage(this, data);
		}
		else {
			ret = ((IInput) this.sender).notifyMessage(this, data);
		}

		if (!(ret instanceof VoidResponse)) {
			for (MediatorListener l : listeners) {
				l.onSendData(this, true, ((DataResponse) ret).getData());
			}
			try {
				Simulator.barrier.await();
			}
			catch (BrokenBarrierException | InterruptedException e) {
				// TODO Auto-generated catch block
				log.debug(LogUtils.MARKER_MEDIATOR_MSG, "broken barrier for mediator : " + this);
			}

		}

		Simulator.barrier.reset();

		return ret;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + sender.getName() + " <-> " + receiver.getName();
	}

}
