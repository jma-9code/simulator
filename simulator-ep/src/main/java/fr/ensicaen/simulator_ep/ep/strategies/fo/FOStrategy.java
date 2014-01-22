package fr.ensicaen.simulator_ep.ep.strategies.fo;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOStrategy.class);

	public FOStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOffice, Mediator m, String data) {
		ISOMsg message8583 = null;
		ComponentIO composantCible = null;
		Mediator mediateurAUtiliser;
		// faire le lien avec le message8583

		/* Si c'est une demande d'autorisation ... */
		try {
			message8583 = ISO8583Tools.read(data);
			switch (message8583.getMTI()) {
				case "0100":
					composantCible = frontOffice.getChild("Acquirer", ComponentIO.class);
					break;

				default:

					break;
			}
		}
		catch (ISO8583Exception | ISOException e) {
			e.printStackTrace();
		}

		mediateurAUtiliser = MediatorFactory.getInstance().getForwardMediator(m, composantCible);
		// forward to the chipset
		return mediateurAUtiliser.send(composantCible, data);

		// Pour Antoine Michels ... ici on traite les messages entrants (ex: une
		// auto arrive ...)
		// Il faut que tu réutilises ta hiérarchie de composant que j'ai remis
		// dans le package test
		// FOUnitTest et que tu routes ces messages aux bons sous composants ...
		// car FOStrategy
		// est le premier niveau et comme une carte par exemple, la réel
		// intelligence est dans la chip
		// donc on route à la chip. Toi tu auras un switch à faire en fct du
		// message de façon à ce que
		// si on branche un tpe sur le fo ça fasse pareil que si n le branchait
		// sur le module destinataire.
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
		// Ici on réagit à des évènements, ils peuvent être programmé par
		// l'utilisateur voire par
		// les stratégies si elles en ont le besoin ... exemple, la compensation
		// est déclenchée
		// en différée (du pdv temporel) par rapport au paiement.
		// Donc en résumé tu dois te poser la question ... est-ce que le FO est
		// maître de la situation
		// dans tel ou tel scénario. Est-ce qu'il doit déclencher une échange
		// avec un autre composant.
		// Tout en considérant la notion hirarchique, normalement au niveau FO
		// on aura rien je pense
		// mais dans les sous module ...
	}

	@Override
	public String toString() {
		return "FO";
	}

}
