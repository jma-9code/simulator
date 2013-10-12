/**
 * 
 */
package model.mediator;

import model.component.IInput;
import model.component.IOutput;
import model.factory.MediatorFactory.EMediator;
import model.response.IResponse;

/**
 * Médiateur de transfert.
 * 
 * Ce médiateur permet le routage vers un sous-composant afin de garder 
 * la référence de la source appelante. Ce médiateur agit comme le principe
 * d'une chaine. Il est donc possible de cumuler plusieurs forward.
 * 
 * Exemple (vue composant) :
 * Card <--> TPE <--> Chipset
 * 
 * Exemple (vue médiateur) :
 * M(Card, TPE) <--> M(TPE, Chipset)
 * 
 * Cas double forward :
 * 
 * 		   Card <-----------------------> Proc
 * M(Card, TPE) <--> M(TPE, Chipset) <--> M(Chipset, Proc)
 * 
 * 		3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3
 *      3									3
 * 		3  2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2  3
 *      3  2 							 2  3
 * 		3  2   1 1 1 1 1 1 1 1 1 1 1 1	 2  3
 * 		3  2   1                     1   2  3
 * 		3  2   1 HalfDuplexMediator  1   2  3
 * 		3  2   1                     1   2  3
 * 		3  2   1 1 1 1 1 1 1 1 1 1 1 1   2  3
 * 		3  2   1 	C1   1 1    C2   1   2  3
 * 		3  2   1 1 1 1 1 1 1 1 1 1 1 1   2  3
 * 		3  2							 2  3
 *  	3  2		   Forward           2  3
 * 		3  2							 2  3
 *  	3  2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2  3
 *  	3  2  	  C1	 2 2	 C3		 2	3
 *  	3  2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2  3
 *      3									3
 *      3			   Forward				3
 *      3									3
 * 		3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3
 *      3		C1		3 3		C4			3
 * 		3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3
 * 
 * @author Flo
 */
public class ForwardMediator extends Mediator {

	/**
	 * Médiateur d'origine re-routé.
	 */
	private Mediator origin;
	
	public ForwardMediator(Mediator origin, IInput forward) {
		super(origin.getSender(), forward);
		this.origin = origin;
	}

	/* (non-Javadoc)
	 * @see model.mediator.Mediator#send(model.component.IOutput, java.lang.String)
	 */
	@Override
	public IResponse send(IOutput s, String data) {
		if (s == sender || s == getRouter(origin)){
			return receiver.input(this, data);
		}
		else if(getOriginType() == EMediator.HALFDUPLEX){
			return ((IInput) sender).input(this, data);
		}
		
		return null;
	}
	
	/**
	 * Méthode de récupération du type de médiateur d'origine.
	 * Cette fonction est récursive.
	 * @return Type
	 */
	private EMediator getOriginType() {
		if(origin instanceof HalfDuplexMediator) {
			return EMediator.HALFDUPLEX;
		}
		else if(origin instanceof ForwardMediator) {
			return ((ForwardMediator) origin).getOriginType();
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
		return "M[Forward - "+origin+" <--> "+receiver+"]";
	}
}
