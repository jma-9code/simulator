/**
 * 
 */
package fr.ensicaen.simulator.model.mediator;

import java.util.concurrent.BrokenBarrierException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Simulator;
import fr.ensicaen.simulator.tools.LogUtils;

/**
 * Médiateur de transfert.
 * 
 * Ce médiateur permet le routage vers un sous-composant afin de garder la
 * référence de la source appelante. Ce médiateur agit comme le principe d'une
 * chaine. Il est donc possible de cumuler plusieurs forward.
 * 
 * Exemple (vue composant) : Card <--> TPE <--> Chipset
 * 
 * Exemple (vue médiateur) : M(Card, TPE) <--> M(TPE, Chipset)
 * 
 * Cas double forward :
 * 
 * Card <-----------------------> Proc M(Card, TPE) <--> M(TPE, Chipset) <-->
 * M(Chipset, Proc)
 * 
 * @author Flo
 */
public class ForwardMediator extends Mediator {

	private static Logger log = LoggerFactory.getLogger(ForwardMediator.class);
	/**
	 * Médiateur d'origine re-routé.
	 */
	private Mediator origin;

	public ForwardMediator() {
		super(null, null);
	}

	public ForwardMediator(Mediator origin, IInput forward) {
		super(origin.getSender(), forward);
		this.origin = origin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.mediator.Mediator#send(model.component.IOutput,
	 * java.lang.String)
	 */
	@Override
	public IResponse send(IOutput s, String data) {
		IResponse ret = null;
		for (MediatorListener l : listeners) {
			l.onSendData(this, s, data);
		}

		try {
			Simulator.barrier.await();
		}
		catch (BrokenBarrierException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(LogUtils.MARKER_MEDIATOR_MSG,
				s.getName() + " send " + data + " to " + ((s.equals(sender)) ? receiver.getName() : sender.getName()));

		if (s == this.sender || s == getRouter(this.origin)) {
			ret = this.receiver.notifyMessage(this, data);
		}
		else if (getOriginType() == EMediator.HALFDUPLEX) {
			ret = ((IInput) this.sender).notifyMessage(this, data);
		}

		Simulator.barrier.reset();
		return ret;
	}

	/**
	 * Méthode de récupération du type de médiateur d'origine. Cette fonction
	 * est récursive.
	 * 
	 * @return Type
	 */
	private EMediator getOriginType() {
		if (this.origin instanceof HalfDuplexMediator) {
			return EMediator.HALFDUPLEX;
		}
		else if (this.origin instanceof ForwardMediator) {
			return ((ForwardMediator) this.origin).getOriginType();
		}
		else {
			return EMediator.SIMPLEX;
		}
	}

	// vocation à rendre plus clair le code.
	private static IOutput getRouter(Mediator origin) {
		return (IOutput) origin.getReceiver();
	}

	@Override
	public String toString() {
		return "M[Forward - " + this.origin + " <--> " + this.receiver + "]";
	}
}
