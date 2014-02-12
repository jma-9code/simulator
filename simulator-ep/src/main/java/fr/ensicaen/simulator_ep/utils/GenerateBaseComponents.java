package fr.ensicaen.simulator_ep.utils;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.factory.DAOFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
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

	public static void main(String[] args) {
		Context.getInstance().autoRegistrationMode();

		componentsProperties();
		// associateMediators();
		// associateStrategies();

		// enregistrement des composants dans la lib
		DAO<Component> comp = DAOFactory.getFactory().getComponentDAO();
		comp.create(card);
		comp.create(ept);
		comp.create(frontOffice);
		comp.create(backOffice);
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
		factory.getMediator(card, chip, EMediator.HALFDUPLEX);
		factory.getMediator(card, magstrippe, EMediator.HALFDUPLEX);

		factory.getMediator(ept, frontOffice, EMediator.HALFDUPLEX);
		factory.getMediator(chipset, frontOffice, EMediator.HALFDUPLEX);
		factory.getMediator(smartCardReader, chipset, EMediator.HALFDUPLEX);
		factory.getMediator(smartCardReader, card, EMediator.HALFDUPLEX);
		factory.getMediator(ept, smartCardReader, EMediator.HALFDUPLEX);
		factory.getMediator(ept, chipset, EMediator.HALFDUPLEX);

		factory.getMediator(frontOffice, ept, EMediator.HALFDUPLEX);
		factory.getMediator(frontOffice, issuer, EMediator.HALFDUPLEX);
		factory.getMediator(frontOffice, acquirer, EMediator.HALFDUPLEX);
		factory.getMediator(issuer, issuerAuthorization, EMediator.HALFDUPLEX);
		factory.getMediator(acquirer, acquirerAuthorization, EMediator.HALFDUPLEX);
		factory.getMediator(acquirerAuthorization, issuerAuthorization, EMediator.HALFDUPLEX);

	}

	public static void componentsProperties() {
		/* CARD */
		card = new ComponentIO("cb");
		card.getProperties().put("pan", "4976710025642130");
		card.getProperties().put("icvv", "000");
		card.getProperties().put("type", "M");
		card.getProperties().put("name", "Florent Moisson");
		card.getProperties().put("date expiration", "09/15");
		chip = new ComponentIO("chip");
		chip.getProperties().put("protocol", "ISO7816");
		chip.getProperties().put("pan", "4976710025642130");
		chip.getProperties().put("bccs", "12421874");
		chip.getProperties().put("ceil", "400");
		chip.getProperties().put("approvalcode", "07B56=");
		chip.getProperties().put("state", "OFF");
		magstrippe = new ComponentIO("magstrippe");
		magstrippe.getProperties().put("iso2", "59859595985888648468454684");
		card.getChilds().add(magstrippe);
		card.getChilds().add(chip);

		/* ETP */
		ept = new ComponentIO("Electronic Payment Terminal");
		ept.setStrategy(new EPTStrategy());
		smartCardReader = new ComponentIO("Smart Card Reader");
		smartCardReader.setStrategy(new EPTSmartCardReaderStrategy());
		ept.getChilds().add(smartCardReader);
		chipset = new ComponentIO("Chipset");
		chipset.setStrategy(new EPTChipsetStrategy());
		chipset.getProperties().put("pos_id", "0000623598");
		chipset.getProperties().put("stan", "000001");
		chipset.getProperties().put("protocol_list", "ISO7816 ISO8583 CB2A-T");
		chipset.getProperties().put("protocol_prefered", "ISO7816");
		ept.getChilds().add(chipset);
		printer = new ComponentIO("Printer");
		ept.getChilds().add(printer);
		securePinPad = new ComponentIO("Secure pin pad");
		ept.getChilds().add(securePinPad);
		networkInterface = new ComponentIO("Network interface");
		ept.getChilds().add(networkInterface);

		/* FO */
		frontOffice = new ComponentIO("FrontOffice");
		issuer = new ComponentIO("Issuer");
		acceptor = new ComponentIO("Acceptor");
		acquirer = new ComponentIO("Acquirer");
		/* Ajout des trois grandes fonctions du front Office */
		frontOffice.getChilds().add(issuer);
		frontOffice.getChilds().add(acceptor);
		frontOffice.getChilds().add(acquirer);

		/* Ajout des modules émetteur */
		issuerAuthorization = new ComponentIO("IssuerAuthorization");
		gestionDeLaFraude = new ComponentIO("GestionDeLaFraude");
		issuer.getChilds().add(issuerAuthorization);
		issuer.getChilds().add(gestionDeLaFraude);

		/* Ajout des composants du module Autorisation */
		controlesCarte = new ComponentIO("controlesCarte");
		traitementsAutorisation = new ComponentIO("traitemntsAutorisation");
		issuerAuthorization.getChilds().add(controlesCarte);
		issuerAuthorization.getChilds().add(traitementsAutorisation);

		gestionDroitsCarte = new ComponentIO("gestionDroitsCarte");
		gestionSoldeCompte = new ComponentIO("gestionSoldeCompte");
		traitementsAutorisation.getChilds().add(gestionDroitsCarte);
		traitementsAutorisation.getChilds().add(gestionSoldeCompte);

		/* Ajout des modules accepteur */
		systemeEncaissement = new ComponentIO("systemeEncaissement");
		concentrateurMonetique = new ComponentIO("concentrateurMonetique");
		telePaiement = new ComponentIO("telePaiement");
		acceptor.getChilds().add(systemeEncaissement);
		acceptor.getChilds().add(concentrateurMonetique);
		acceptor.getChilds().add(telePaiement);

		/* Ajout des composants du module systemeEncaissement */
		gestionEncaissementsMultiples = new ComponentIO("gestionEncaissementsMultiples");
		gestionRolesDeCaisse = new ComponentIO("gestionRolesDeCaisse");
		gestionTickets = new ComponentIO("gestionTickets");
		gestionPeripheriques = new ComponentIO("gestionPeripheriques");
		editionDeFactures = new ComponentIO("editionDeFactures");
		systemeEncaissement.getChilds().add(gestionEncaissementsMultiples);
		systemeEncaissement.getChilds().add(gestionRolesDeCaisse);
		systemeEncaissement.getChilds().add(gestionTickets);
		systemeEncaissement.getChilds().add(gestionPeripheriques);
		systemeEncaissement.getChilds().add(editionDeFactures);

		/* Ajout des composants du module concentrateur Monétique */
		gestionLigneDeCaisse = new ComponentIO("gestionLigneDeCaisse");
		gestionTerminauxDePaiementGrappes = new ComponentIO("gestionTerminauxDePaiementGrappes");
		gestionRolesDeCaisse.getChilds().add(gestionLigneDeCaisse);
		gestionRolesDeCaisse.getChilds().add(gestionTerminauxDePaiementGrappes);

		/* Ajout des composants du module télépaiement */
		passerelleTelepaiement = new ComponentIO("passerelleTelepaiement");
		gestionnaireTelepaiement = new ComponentIO("gestionnaireTelepaiement");
		telePaiement.getChilds().add(passerelleTelepaiement);
		telePaiement.getChilds().add(gestionnaireTelepaiement);

		acceptationPaiementPubliphone = new ComponentIO("acceptationPaiementPubliphone");
		acceptationPaiementParInternet = new ComponentIO("acceptationPaiementParInternet");
		acceptationPaiementParGSM = new ComponentIO("acceptationPaiementParGSM");
		passerelleTelepaiement.getChilds().add(acceptationPaiementPubliphone);
		passerelleTelepaiement.getChilds().add(acceptationPaiementParInternet);
		passerelleTelepaiement.getChilds().add(acceptationPaiementParGSM);

		delivrancePaiement = new ComponentIO("delivrancePaiement");
		gestionDesRemises = new ComponentIO("gestionDesRemises");
		gestionDonneesFonctionnement = new ComponentIO("gestionDonneesFonctionnement");
		gestionnaireTelepaiement.getChilds().add(delivrancePaiement);
		gestionnaireTelepaiement.getChilds().add(gestionDesRemises);
		gestionnaireTelepaiement.getChilds().add(gestionDonneesFonctionnement);

		/* Ajout des modules acquéreur */
		acquirerAuthorization = new ComponentIO("AcquirerAuthorization");
		GABHandler = new ComponentIO("GABHandler");
		telecollection = new ComponentIO("telecollection");
		paymentAcquirer = new ComponentIO("paymentAcquirer");
		compensationSingleMessage = new ComponentIO("compensationSingleMessage");
		acquirer.getChilds().add(acquirerAuthorization);
		acquirer.getChilds().add(GABHandler);
		acquirer.getChilds().add(telecollection);
		acquirer.getChilds().add(paymentAcquirer);
		acquirer.getChilds().add(compensationSingleMessage);

		/* Ajout des composants du module GABHandler" */
		retrait = new ComponentIO("retrait");
		libreServiceBancaire = new ComponentIO("libreServiceBancaire");
		GABHandler.getChilds().add(retrait);
		GABHandler.getChilds().add(libreServiceBancaire);

		retraitAutoCompte = new ComponentIO("retraitAutoCompte");
		depot = new ComponentIO("depot");
		virement = new ComponentIO("virement");
		commandeDeChequier = new ComponentIO("commandeDeChequier");
		demandeDeRIB = new ComponentIO("demandeDeRIB");
		demandeDeSolde = new ComponentIO("demandeDeSolde");
		historiqueOperations = new ComponentIO("historiqueOperations");
		libreServiceBancaire.getChilds().add(retraitAutoCompte);
		libreServiceBancaire.getChilds().add(depot);
		libreServiceBancaire.getChilds().add(virement);
		libreServiceBancaire.getChilds().add(commandeDeChequier);
		libreServiceBancaire.getChilds().add(demandeDeRIB);
		libreServiceBancaire.getChilds().add(demandeDeSolde);
		libreServiceBancaire.getChilds().add(historiqueOperations);

		/* Ajout des composants du module telecollection */
		gestionCBPRCB2A = new ComponentIO("gestionCBPRCB2A");
		telecollection.getChilds().add(gestionCBPRCB2A);

		/* Ajout des composants du module paymentAcquirer */
		paiementDeProximite = new ComponentIO("paiementDeProximite");
		preAutorisation = new ComponentIO("preAutorisation");
		venteADistance = new ComponentIO("venteADistance");
		telePaiementGSM = new ComponentIO("telePaiementGSM");
		paiementVocal = new ComponentIO("paiementVocal");
		paiementTelevise = new ComponentIO("paiementTelevise");
		quasiCash = new ComponentIO("quasiCash");
		cashAdvance = new ComponentIO("cashAdvance");
		paymentAcquirer.getChilds().add(paiementDeProximite);
		paymentAcquirer.getChilds().add(preAutorisation);
		paymentAcquirer.getChilds().add(venteADistance);
		paymentAcquirer.getChilds().add(telePaiementGSM);
		paymentAcquirer.getChilds().add(paiementVocal);
		paymentAcquirer.getChilds().add(paiementTelevise);
		paymentAcquirer.getChilds().add(quasiCash);
		paymentAcquirer.getChilds().add(cashAdvance);

		/* BO */
		backOffice = new ComponentIO("BackOffice");

	}
}
