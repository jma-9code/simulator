package fr.ensicaen.simulator_ep.utils;

import java.util.HashMap;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.dao.factory.DAOFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.ep.strategies.card.CardChipStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.card.CardStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTChipsetStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTSmartCardReaderStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerAuthorizationStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.issuer.FOIssuerAuthorizationStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.issuer.FOIssuerStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.network.GenericNetworkStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.network.GenericRouterStrategy;

public class GenerateBaseComponents {

	private static MediatorFactory factory = MediatorFactory.getInstance();

	/* CARD */
	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;

	/* ETP */
	private static ComponentIO ept;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;

	/* FO */
	private static ComponentIO frontOffice;
	// Les trois grandes fonctions d'un FO
	private static ComponentIO issuer;
	private static ComponentIO acceptor;
	private static ComponentIO acquirer;

	// Différents modules de la fonction émetteur */
	private static ComponentIO issuerAuthorization;
	private static ComponentIO controlesSecurite;
	private static ComponentIO controlesCarte;
	private static ComponentIO traitementsAutorisation;
	private static ComponentIO gestionDroitsCarte;
	private static ComponentIO gestionSoldeCompte;
	private static ComponentIO gestionDeLaFraude;

	// Différents modules de la fonction accepteur */
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

	// Différents modules de la fonction acquéreur */
	private static ComponentIO acquirerAuthorization;
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
	private static ComponentIO paymentAcquirer;
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

	/* BO */
	private static ComponentIO backOffice;

	/* network router */
	private static ComponentIO router;

	/* networks */
	private static ComponentIO eRSBNetwork;

	public static void main(String[] args) {
		Context.getInstance().autoRegistrationMode();

		componentsProperties();
		associateMediators();
		associateStrategies();

		// enregistrement des composants dans la lib
		DAO<Component> comp = DAOFactory.getFactory().getComponentDAO();
		comp.create(card);
		comp.create(ept);
		comp.create(frontOffice);
		comp.create(backOffice);
		comp.create(router);
		comp.create(eRSBNetwork);

		ScenarioData sc = new ScenarioData("test", Context.getInstance(), new HashMap<String, Object>());
		DAO<ScenarioData> sce = DAOFactory.getFactory().getScenarioDataDAO();
		sce.create(sc);

	}

	public static void associateStrategies() {
		// card
		card.setStrategy(new CardStrategy());
		chip.setStrategy(new CardChipStrategy());

		// etp
		ept.setStrategy(new EPTStrategy());
		smartCardReader.setStrategy(new EPTSmartCardReaderStrategy());
		chipset.setStrategy(new EPTChipsetStrategy());

		// fo
		frontOffice.setStrategy(new FOStrategy());
		acquirer.setStrategy(new FOAcquirerStrategy());
		acquirerAuthorization.setStrategy(new FOAcquirerAuthorizationStrategy());
		issuer.setStrategy(new FOIssuerStrategy());
		issuerAuthorization.setStrategy(new FOIssuerAuthorizationStrategy());
	}

	public static void associateMediators() {
		factory.getMediator(card, ept, EMediator.HALFDUPLEX);
		factory.getMediator(ept, frontOffice, EMediator.HALFDUPLEX);
		factory.getMediator(frontOffice, router, EMediator.HALFDUPLEX);
		factory.getMediator(router, eRSBNetwork, EMediator.HALFDUPLEX);

		Mediator m = factory.getMediator(frontOffice, eRSBNetwork, EMediator.HALFDUPLEX);
		m.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, "e-RSB");
	}

	public static void componentsProperties() {
		/* CARD */
		card = new ComponentIO(CommonNames.CARD);
		card.getProperties().put("pan", "4976710025642130");
		card.getProperties().put("icvv", "000");
		card.getProperties().put("type", "M");
		card.getProperties().put("name", "Florent Moisson");
		card.getProperties().put("date expiration", "09/15");
		chip = new ComponentIO(CommonNames.CARD_CHIP);
		chip.getProperties().put("protocol", "ISO7816");
		chip.getProperties().put("pan", "4976710025642130");
		chip.getProperties().put("bccs", "12421874");
		chip.getProperties().put("ceil", "400");
		chip.getProperties().put("approvalcode", "07B56=");
		chip.getProperties().put("state", "OFF");
		magstrippe = new ComponentIO(CommonNames.CARD_MAGSTRIPPE);
		magstrippe.getProperties().put("iso2", "59859595985888648468454684");
		card.addChild(magstrippe);
		card.addChild(chip);

		/* ETP */
		ept = new ComponentIO(CommonNames.ETP);
		ept.setStrategy(new EPTStrategy());
		smartCardReader = new ComponentIO(CommonNames.ETP_SMARTCARDREADER);
		smartCardReader.setStrategy(new EPTSmartCardReaderStrategy());
		ept.addChild(smartCardReader);
		chipset = new ComponentIO(CommonNames.ETP_CHIPSET);
		chipset.setStrategy(new EPTChipsetStrategy());
		chipset.getProperties().put("pos_id", "0000623598");
		chipset.getProperties().put("acceptor_id", "0000623598");
		chipset.getProperties().put("posdatacode", "510101511326105");
		chipset.getProperties().put("stan", "000001");
		chipset.getProperties().put("protocol_list", "ISO7816 ISO8583 CB2A-T");
		chipset.getProperties().put("protocol_prefered", "ISO7816");
		chipset.getProperties().put("pin_enter", "1234");
		ept.addChild(chipset);
		printer = new ComponentIO("Printer");
		ept.addChild(printer);
		securePinPad = new ComponentIO("Secure pin pad");
		ept.addChild(securePinPad);
		networkInterface = new ComponentIO("Network interface");
		ept.addChild(networkInterface);

		/* FO */
		frontOffice = new ComponentIO(CommonNames.FO);
		issuer = new ComponentIO(CommonNames.FO_ISSUER);
		acceptor = new ComponentIO("Acceptor");
		acquirer = new ComponentIO(CommonNames.FO_ACQUIRER);
		/* Ajout des trois grandes fonctions du front Office */
		frontOffice.addChild(issuer);
		frontOffice.addChild(acceptor);
		frontOffice.addChild(acquirer);

		/* Ajout des modules émetteur */
		issuerAuthorization = new ComponentIO(CommonNames.FO_ISSUER_AUTH);
		gestionDeLaFraude = new ComponentIO("GestionDeLaFraude");
		issuer.addChild(issuerAuthorization);
		issuer.addChild(gestionDeLaFraude);

		/* Ajout des composants du module Autorisation */
		controlesCarte = new ComponentIO("controlesCarte");
		traitementsAutorisation = new ComponentIO("traitemntsAutorisation");
		issuerAuthorization.addChild(controlesCarte);
		issuerAuthorization.addChild(traitementsAutorisation);

		gestionDroitsCarte = new ComponentIO("gestionDroitsCarte");
		gestionSoldeCompte = new ComponentIO("gestionSoldeCompte");
		traitementsAutorisation.addChild(gestionDroitsCarte);
		traitementsAutorisation.addChild(gestionSoldeCompte);

		/* Ajout des modules accepteur */
		systemeEncaissement = new ComponentIO("systemeEncaissement");
		concentrateurMonetique = new ComponentIO("concentrateurMonetique");
		telePaiement = new ComponentIO("telePaiement");
		acceptor.addChild(systemeEncaissement);
		acceptor.addChild(concentrateurMonetique);
		acceptor.addChild(telePaiement);

		/* Ajout des composants du module systemeEncaissement */
		gestionEncaissementsMultiples = new ComponentIO("gestionEncaissementsMultiples");
		gestionRolesDeCaisse = new ComponentIO("gestionRolesDeCaisse");
		gestionTickets = new ComponentIO("gestionTickets");
		gestionPeripheriques = new ComponentIO("gestionPeripheriques");
		editionDeFactures = new ComponentIO("editionDeFactures");
		systemeEncaissement.addChild(gestionEncaissementsMultiples);
		systemeEncaissement.addChild(gestionRolesDeCaisse);
		systemeEncaissement.addChild(gestionTickets);
		systemeEncaissement.addChild(gestionPeripheriques);
		systemeEncaissement.addChild(editionDeFactures);

		/* Ajout des composants du module concentrateur Monétique */
		gestionLigneDeCaisse = new ComponentIO("gestionLigneDeCaisse");
		gestionTerminauxDePaiementGrappes = new ComponentIO("gestionTerminauxDePaiementGrappes");
		gestionRolesDeCaisse.addChild(gestionLigneDeCaisse);
		gestionRolesDeCaisse.addChild(gestionTerminauxDePaiementGrappes);

		/* Ajout des composants du module télépaiement */
		passerelleTelepaiement = new ComponentIO("passerelleTelepaiement");
		gestionnaireTelepaiement = new ComponentIO("gestionnaireTelepaiement");
		telePaiement.addChild(passerelleTelepaiement);
		telePaiement.addChild(gestionnaireTelepaiement);

		acceptationPaiementPubliphone = new ComponentIO("acceptationPaiementPubliphone");
		acceptationPaiementParInternet = new ComponentIO("acceptationPaiementParInternet");
		acceptationPaiementParGSM = new ComponentIO("acceptationPaiementParGSM");
		passerelleTelepaiement.addChild(acceptationPaiementPubliphone);
		passerelleTelepaiement.addChild(acceptationPaiementParInternet);
		passerelleTelepaiement.addChild(acceptationPaiementParGSM);

		delivrancePaiement = new ComponentIO("delivrancePaiement");
		gestionDesRemises = new ComponentIO("gestionDesRemises");
		gestionDonneesFonctionnement = new ComponentIO("gestionDonneesFonctionnement");
		gestionnaireTelepaiement.addChild(delivrancePaiement);
		gestionnaireTelepaiement.addChild(gestionDesRemises);
		gestionnaireTelepaiement.addChild(gestionDonneesFonctionnement);

		/* Ajout des modules acquéreur */
		acquirerAuthorization = new ComponentIO(CommonNames.FO_ACQUIRER_AUTH);
		GABHandler = new ComponentIO("GABHandler");
		telecollection = new ComponentIO("telecollection");
		paymentAcquirer = new ComponentIO("paymentAcquirer");
		compensationSingleMessage = new ComponentIO("compensationSingleMessage");
		acquirer.addChild(acquirerAuthorization);
		acquirer.addChild(GABHandler);
		acquirer.addChild(telecollection);
		acquirer.addChild(paymentAcquirer);
		acquirer.addChild(compensationSingleMessage);

		/* Ajout des composants du module GABHandler" */
		retrait = new ComponentIO("retrait");
		libreServiceBancaire = new ComponentIO("libreServiceBancaire");
		GABHandler.addChild(retrait);
		GABHandler.addChild(libreServiceBancaire);

		retraitAutoCompte = new ComponentIO("retraitAutoCompte");
		depot = new ComponentIO("depot");
		virement = new ComponentIO("virement");
		commandeDeChequier = new ComponentIO("commandeDeChequier");
		demandeDeRIB = new ComponentIO("demandeDeRIB");
		demandeDeSolde = new ComponentIO("demandeDeSolde");
		historiqueOperations = new ComponentIO("historiqueOperations");
		libreServiceBancaire.addChild(retraitAutoCompte);
		libreServiceBancaire.addChild(depot);
		libreServiceBancaire.addChild(virement);
		libreServiceBancaire.addChild(commandeDeChequier);
		libreServiceBancaire.addChild(demandeDeRIB);
		libreServiceBancaire.addChild(demandeDeSolde);
		libreServiceBancaire.addChild(historiqueOperations);

		/* Ajout des composants du module telecollection */
		gestionCBPRCB2A = new ComponentIO("gestionCBPRCB2A");
		telecollection.addChild(gestionCBPRCB2A);

		/* Ajout des composants du module paymentAcquirer */
		paiementDeProximite = new ComponentIO("paiementDeProximite");
		preAutorisation = new ComponentIO("preAutorisation");
		venteADistance = new ComponentIO("venteADistance");
		telePaiementGSM = new ComponentIO("telePaiementGSM");
		paiementVocal = new ComponentIO("paiementVocal");
		paiementTelevise = new ComponentIO("paiementTelevise");
		quasiCash = new ComponentIO("quasiCash");
		cashAdvance = new ComponentIO("cashAdvance");
		paymentAcquirer.addChild(paiementDeProximite);
		paymentAcquirer.addChild(preAutorisation);
		paymentAcquirer.addChild(venteADistance);
		paymentAcquirer.addChild(telePaiementGSM);
		paymentAcquirer.addChild(paiementVocal);
		paymentAcquirer.addChild(paiementTelevise);
		paymentAcquirer.addChild(quasiCash);
		paymentAcquirer.addChild(cashAdvance);

		/* BO */
		backOffice = new ComponentIO("BackOffice");

		/* Network */
		eRSBNetwork = new ComponentIO("Network");
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEY_NAME, "e-RSB");
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEYPREFIX_ISSUER_OF + "49767", "Issuer1");

		/* Router */
		router = new ComponentIO("Router");
		router.setStrategy(new GenericRouterStrategy());
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "4", "e-RSB");
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "5", "e-RSB");

	}
}
