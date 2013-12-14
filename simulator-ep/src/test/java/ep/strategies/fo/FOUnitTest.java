package ep.strategies.fo;

import model.component.ComponentIO;

import org.junit.After;
import org.junit.Before;

import simulator.Context;

public class FOUnitTest {

	private static ComponentIO frontOffice;

	/* Les trois grandes fonctions d'un FO */
	private static ComponentIO transmitter;
	private static ComponentIO acceptor;
	private static ComponentIO purchaser;

	/* Différents modules de la fonction émetteur */

	private static ComponentIO authorization;
	private static ComponentIO controlesSecurite;
	private static ComponentIO controlesCarte;
	private static ComponentIO traitementsAutorisation;
	private static ComponentIO gestionDroitsCarte;
	private static ComponentIO gestionSoldeCompte;

	private static ComponentIO gestionDeLaFraude;

	/* Différents modules de la fonction accepteur */

	private static ComponentIO systemeEncaissement;
	private static ComponentIO gestionEncaissementsMultiples;
	private static ComponentIO gestionRolesDeCaisse;
	private static ComponentIO gestionRemises;
	private static ComponentIO gestionTickets;
	private static ComponentIO gestionPeripheriques;
	private static ComponentIO editionDeFactures;

	private static ComponentIO concentrateurMonetique;
	private static ComponentIO gestionLigneDeCaisse;
	private static ComponentIO gestionTerminauxDePaiementGrappes;

	private static ComponentIO telePaiement;
	private static ComponentIO passerelleTelepaiement;
	private static ComponentIO acceptationPaiementPubliphone;
	private static ComponentIO acceptationPaiementParInternet;
	private static ComponentIO acceptationPaiementParGSM;

	private static ComponentIO gestionnaireTelepaiement;
	private static ComponentIO delivrancePaiement;
	private static ComponentIO gestionDesRemises;
	private static ComponentIO gestionDonneesFonctionnement;

	/* Différents modules de la fonction acquéreur */

	private static ComponentIO GABHandler;

	private static ComponentIO retrait;
	private static ComponentIO libreServiceBancaire;
	private static ComponentIO retraitAutoCompte;
	private static ComponentIO depot;
	private static ComponentIO virement;
	private static ComponentIO commandeDeChequier;
	private static ComponentIO demandeDeRIB;
	private static ComponentIO demandeDeSolde;
	private static ComponentIO historiqueOperations;

	private static ComponentIO telecollection;
	private static ComponentIO gestionCBPRCB2A;

	private static ComponentIO paymentPurchaser;

	private static ComponentIO paiementDeProximite;
	private static ComponentIO preAutorisation;
	private static ComponentIO venteADistance;
	private static ComponentIO paiementSurAutomate;
	private static ComponentIO telePaiementGSM;
	private static ComponentIO paiementVocal;
	private static ComponentIO paiementTelevise;
	private static ComponentIO quasiCash;
	private static ComponentIO cashAdvance;

	private static ComponentIO compensationSingleMessage;

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();
		frontOffice = new ComponentIO("FrontOffice");

		transmitter = new ComponentIO("transmitter");
		acceptor = new ComponentIO("acceptor");
		purchaser = new ComponentIO("purchaser");

		/* Ajout des trois grandes fonctions du front Office */
		frontOffice.getComponents().add(1, transmitter);
		frontOffice.getComponents().add(2, acceptor);
		frontOffice.getComponents().add(3, purchaser);

		/* Ajout des modules émetteur */
		authorization = new ComponentIO("authorization");
		gestionDeLaFraude = new ComponentIO("gestionDeLaFraude");
		frontOffice.getComponents().get(1).getComponents().add(1, authorization);
		frontOffice.getComponents().get(1).getComponents().add(2, gestionDeLaFraude);

		/* Ajout des composants du module Autorisation */
		controlesCarte = new ComponentIO("controlesCarte");
		traitementsAutorisation = new ComponentIO("traitemntsAutorisation");
		frontOffice.getComponents().get(1).getComponents().get(1).getComponents().add(1, controlesCarte);
		frontOffice.getComponents().get(1).getComponents().get(1).getComponents().add(2, traitementsAutorisation);

		gestionDroitsCarte = new ComponentIO("gestionDroitsCarte");
		gestionSoldeCompte = new ComponentIO("gestionSoldeCompte");
		frontOffice.getComponents().get(1).getComponents().get(1).getComponents().get(2).getComponents()
				.add(1, gestionDroitsCarte);
		frontOffice.getComponents().get(1).getComponents().get(1).getComponents().get(2).getComponents()
				.add(2, gestionSoldeCompte);

		/* Ajout des modules accepteur */
		systemeEncaissement = new ComponentIO("systemeEncaissement");
		concentrateurMonetique = new ComponentIO("concentrateurMonetique");
		telePaiement = new ComponentIO("telePaiement");
		frontOffice.getComponents().get(2).getComponents().add(1, systemeEncaissement);
		frontOffice.getComponents().get(2).getComponents().add(2, concentrateurMonetique);
		frontOffice.getComponents().get(2).getComponents().add(3, telePaiement);

		/* Ajout des composants du module systemeEncaissement */
		gestionEncaissementsMultiples = new ComponentIO("gestionEncaissementsMultiples");
		gestionRolesDeCaisse = new ComponentIO("gestionRolesDeCaisse");
		gestionTickets = new ComponentIO("gestionTickets");
		gestionPeripheriques = new ComponentIO("gestionPeripheriques");
		editionDeFactures = new ComponentIO("editionDeFactures");
		frontOffice.getComponents().get(2).getComponents().get(1).getComponents().add(1, gestionEncaissementsMultiples);
		frontOffice.getComponents().get(2).getComponents().get(1).getComponents().add(2, gestionRolesDeCaisse);
		frontOffice.getComponents().get(2).getComponents().get(1).getComponents().add(3, gestionTickets);
		frontOffice.getComponents().get(2).getComponents().get(1).getComponents().add(4, gestionPeripheriques);
		frontOffice.getComponents().get(2).getComponents().get(1).getComponents().add(5, editionDeFactures);

		/* Ajout des composants du module concentrateur Monétique */
		gestionLigneDeCaisse = new ComponentIO("gestionLigneDeCaisse");
		gestionTerminauxDePaiementGrappes = new ComponentIO("gestionTerminauxDePaiementGrappes");
		frontOffice.getComponents().get(2).getComponents().get(2).getComponents().add(1, gestionLigneDeCaisse);
		frontOffice.getComponents().get(2).getComponents().get(2).getComponents()
				.add(2, gestionTerminauxDePaiementGrappes);

		/* Ajout des composants du module télépaiement */
		passerelleTelepaiement = new ComponentIO("passerelleTelepaiement");
		gestionnaireTelepaiement = new ComponentIO("gestionnaireTelepaiement");
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().add(1, passerelleTelepaiement);
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().add(2, gestionnaireTelepaiement);

		acceptationPaiementPubliphone = new ComponentIO("acceptationPaiementPubliphone");
		acceptationPaiementParInternet = new ComponentIO("acceptationPaiementParInternet");
		acceptationPaiementParGSM = new ComponentIO("acceptationPaiementParGSM");
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(1).getComponents()
				.add(1, acceptationPaiementPubliphone);
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(1).getComponents()
				.add(2, acceptationPaiementParInternet);
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(1).getComponents()
				.add(3, acceptationPaiementParGSM);

		delivrancePaiement = new ComponentIO("delivrancePaiement");
		gestionDesRemises = new ComponentIO("gestionDesRemises");
		gestionDonneesFonctionnement = new ComponentIO("gestionDonneesFonctionnement");
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(2).getComponents()
				.add(1, delivrancePaiement);
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(2).getComponents()
				.add(1, gestionDesRemises);
		frontOffice.getComponents().get(2).getComponents().get(3).getComponents().get(2).getComponents()
				.add(1, gestionDonneesFonctionnement);

		/* Ajout des modules acquéreur */

		GABHandler = new ComponentIO("GABHandler");
		telecollection = new ComponentIO("telecollection");
		paymentPurchaser = new ComponentIO("paymentPurchaser");
		compensationSingleMessage = new ComponentIO("compensationSingleMessage");
		frontOffice.getComponents().get(3).getComponents().add(1, GABHandler);
		frontOffice.getComponents().get(3).getComponents().add(2, telecollection);
		frontOffice.getComponents().get(3).getComponents().add(3, paymentPurchaser);
		frontOffice.getComponents().get(3).getComponents().add(4, compensationSingleMessage);

		/* Ajout des composants du module GABHandler" */
		retrait = new ComponentIO("retrait");
		libreServiceBancaire = new ComponentIO("libreServiceBancaire");
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().add(1, retrait);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().add(2, libreServiceBancaire);

		retraitAutoCompte = new ComponentIO("retraitAutoCompte");
		depot = new ComponentIO("depot");
		virement = new ComponentIO("virement");
		commandeDeChequier = new ComponentIO("commandeDeChequier");
		demandeDeRIB = new ComponentIO("demandeDeRIB");
		demandeDeSolde = new ComponentIO("demandeDeSolde");
		historiqueOperations = new ComponentIO("historiqueOperations");
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(1, retraitAutoCompte);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents().add(2, depot);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(3, virement);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(4, commandeDeChequier);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(5, demandeDeRIB);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(6, demandeDeSolde);
		frontOffice.getComponents().get(3).getComponents().get(1).getComponents().get(2).getComponents()
				.add(7, historiqueOperations);

		/* Ajout des composants du module telecollection */
		gestionCBPRCB2A = new ComponentIO("gestionCBPRCB2A");
		frontOffice.getComponents().get(3).getComponents().get(2).getComponents().add(1, gestionCBPRCB2A);

		/* Ajout des composants du module paymentPurchaser */
		paiementDeProximite = new ComponentIO("paiementDeProximite");
		preAutorisation = new ComponentIO("preAutorisation");
		venteADistance = new ComponentIO("venteADistance");
		telePaiementGSM = new ComponentIO("telePaiementGSM");
		paiementVocal = new ComponentIO("paiementVocal");
		paiementTelevise = new ComponentIO("paiementTelevise");
		quasiCash = new ComponentIO("quasiCash");
		cashAdvance = new ComponentIO("cashAdvance");
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(1, paiementDeProximite);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(2, preAutorisation);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(3, venteADistance);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(4, telePaiementGSM);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(5, paiementVocal);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(6, paiementTelevise);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(7, quasiCash);
		frontOffice.getComponents().get(3).getComponents().get(3).getComponents().add(8, cashAdvance);

	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

}
