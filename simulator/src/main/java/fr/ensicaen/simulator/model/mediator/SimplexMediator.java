package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.Simulator;
import fr.ensicaen.simulator.tools.LogUtils;

/**
 * Permet d'envoyer un message dans un seul sens (unidirectionnel) Rq :
 * Utilisable uniquement si le composant emeteur a une sortie, et que le
 * composant recepteur a une entree
 * 
 * @author JM
 * 
 */
public class SimplexMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(SimplexMediator.class);

	public SimplexMediator() {
		super(null, null);
	}

	public SimplexMediator(IOutput _sender, IInput _receiver) {
		super(_sender, _receiver);

		// auto register
		Context.getInstance().registerMediator(this, true);
	}

	/**
	 * Envoie d'un msg de A vers B
	 * 
	 * @param data
	 */
	@Override
	public VoidResponse send(IOutput c, String data) {
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

		IResponse response = this.receiver.notifyMessage(this, data);
		if (response == null || !(response instanceof VoidResponse)) {
			log.error("Invalid simplex response : must return VoidResponse !");
		}

		Simulator.barrier.reset();
		return (VoidResponse) response;
	}

	@Override
	public String toString() {
		return "M[Simplex - " + this.sender + " --> " + this.receiver + "]";
	}
}
