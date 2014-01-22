package ep.strategies.fo;

import java.util.Calendar;
import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.SimulatorFactory;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerAuthorizationStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.issuer.FOIssuerAuthorizationStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.issuer.FOIssuerStrategy;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOUnitTest {

	private static ComponentIO ept;
	private static Mediator m_ept_fo;
	private static Mediator m_fo_foIssuer;
	private static Mediator m_fo_foAcquirer;
	private static Mediator m_foIssuer_FoIssuerAuthorization;
	private static Mediator m_foAcquirer_FoAcquirerAuthorization;
	private static Mediator m_foAcquirerAuthorization_foIssuerAuthorization;
	MediatorFactory factory = MediatorFactory.getInstance();

	private static ComponentIO frontOffice;

	/* Les trois grandes fonctions d'un FO */
	private static ComponentIO issuer;
	private static ComponentIO acceptor;
	private static ComponentIO acquirer;

	/* Différents modules de la fonction émetteur */

	private static ComponentIO issuerAuthorization;
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

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();
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

		frontOffice.setStrategy(new FOStrategy());
		acquirer.setStrategy(new FOAcquirerStrategy());
		acquirerAuthorization.setStrategy(new FOAcquirerAuthorizationStrategy());
		issuer.setStrategy(new FOIssuerStrategy());
		issuerAuthorization.setStrategy(new FOIssuerAuthorizationStrategy());

		ept = new ComponentIO("Electronic Payment Terminal");

		m_ept_fo = MediatorFactory.getInstance().getMediator(frontOffice, ept, EMediator.HALFDUPLEX);
		m_fo_foIssuer = MediatorFactory.getInstance().getMediator(frontOffice, issuer, EMediator.HALFDUPLEX);
		m_fo_foAcquirer = MediatorFactory.getInstance().getMediator(frontOffice, acquirer, EMediator.HALFDUPLEX);
		m_foIssuer_FoIssuerAuthorization = MediatorFactory.getInstance().getMediator(issuer, issuerAuthorization,
				EMediator.HALFDUPLEX);
		m_foAcquirer_FoAcquirerAuthorization = MediatorFactory.getInstance().getMediator(acquirer,
				acquirerAuthorization, EMediator.HALFDUPLEX);
		m_foAcquirerAuthorization_foIssuerAuthorization = MediatorFactory.getInstance().getMediator(
				acquirerAuthorization, issuerAuthorization, EMediator.HALFDUPLEX);

		ept.setStrategy(new IStrategy<ComponentIO>() {

			private static final long serialVersionUID = 2626955719622250036L;

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				// TODO Auto-generated method stub
				System.out.println("evennnnnnnnnnnnnnnnnnnnnnnt !!!!");
				ISOMsg authorizationRequest = new ISOMsg();
				try {
					authorizationRequest.setPackager(ISO8583Tools.getPackager());
					authorizationRequest.setMTI("0100");
					authorizationRequest.set(2, "0123456789123456"); // PAN
					authorizationRequest.set(3, "000101"); // Type of Auth +
															// accounts
					authorizationRequest.set(4, "100"); // 100€
					authorizationRequest.set(7, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
																												// :
																												// MMDDhhmmss
					authorizationRequest.set(11, "000001"); // System Trace
															// Audit Number
					authorizationRequest.set(38, "123456"); // Approval Code
					authorizationRequest.set(42, "623598"); // Acceptor's ID
					authorizationRequest.set(123, "21151168"); // POS Data Code
				}
				catch (ISOException e) {
					e.printStackTrace();
				}

				ISOMsg authorizationAnswer = null;
				try {
					authorizationAnswer = new ISOMsg();
					authorizationAnswer.setPackager(ISO8583Tools.getPackager());
					authorizationAnswer.unpack(((DataResponse) m_ept_fo.send(_this,
							new String(authorizationRequest.pack()))).getData().getBytes());
					Assert.assertTrue(authorizationAnswer.getValue(39).equals("00"));
				}
				catch (ISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void init(IOutput _this, Context ctx) {
				// TODO Auto-generated method stub
				ctx.subscribeEvent(_this, "AUTHORIZATION_CALL");
			}
		});

	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void testAppelAutorisation() {
		// on insert la carte dans le tpe, le tpe envoie des donnees a la carte
		Context.getInstance().addStartPoint(new Date(), "AUTHORIZATION_CALL");
		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		}

	}

}
