package ep.strategies.fo;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator_ep.ep.strategies.fo.FOStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerRemoteDataCollectionStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer.FOAcquirerStrategy;
import fr.ensicaen.simulator_ep.utils.ComponentEP;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOAcquirerRemoteDataCollectionUnitTest {

	/* Fake component */
	private static ComponentIO fakeComponent;

	/* Composant racine : Front office */
	private static ComponentIO frontOffice;

	/* Fonction acquéreur */
	private static ComponentIO acquirer;

	/* Fonction télé-collecte */
	private static ComponentIO remoteDataCollection;

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();

		/* Init component */
		frontOffice = new ComponentIO("Front Office", ComponentEP.FRONT_OFFICE.ordinal());
		acquirer = new ComponentIO("Acquirer", ComponentEP.FO_ACQUIRER.ordinal());
		remoteDataCollection = new ComponentIO("Remote Data Collection",
				ComponentEP.FO_ACQUIRER_REMOTE_DATA_COLLECTION.ordinal());

		/* Settings kinship */
		frontOffice.addChild(acquirer);
		acquirer.addChild(remoteDataCollection);

		/* Settings strategies */
		frontOffice.setStrategy(new FOStrategy());
		acquirer.setStrategy(new FOAcquirerStrategy());
		remoteDataCollection.setStrategy(new FOAcquirerRemoteDataCollectionStrategy());
	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void testSignOn() throws ISOException {
		// sign on 0804
		IResponse res = remoteDataCollection.notifyMessage(null, msg0804());
		Assert.assertFalse(res.isVoid());

		//
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0814");
			Assert.assertEquals(msg.getValue(39), "0000");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void testSubmissionHeader() throws ISOException {
		// init transfert 0306
		IResponse res = remoteDataCollection.notifyMessage(null, msg0306());
		Assert.assertFalse(res.isVoid());

		//
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0316");
			Assert.assertEquals(msg.getValue(39), "0000");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void testFinancialTransaction() throws ISOException {
		// init transfert 0306 avec 3 messages à envoyer
		IResponse res = remoteDataCollection.notifyMessage(null, msg0306());

		// financial transaction 0246
		res = remoteDataCollection.notifyMessage(null, msg0246());
		Assert.assertTrue(res.isVoid());
		res = remoteDataCollection.notifyMessage(null, msg0246());
		Assert.assertTrue(res.isVoid());
		res = remoteDataCollection.notifyMessage(null, msg0246());
		Assert.assertFalse(res.isVoid());

		// controle reponse
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0256");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}

		// controle etat fo
		Assert.assertEquals(
				remoteDataCollection.getProperty(FOAcquirerRemoteDataCollectionStrategy.CKEY_CURRENT_MESSAGE), "3");
		Assert.assertEquals(remoteDataCollection.getProperty(FOAcquirerRemoteDataCollectionStrategy.CKEY_NB_MESSAGE),
				"000003");
		Assert.assertEquals(remoteDataCollection.getProperty(FOAcquirerRemoteDataCollectionStrategy.CKEY_TOTAL_AMOUNT),
				"24000");

		// test consolidation
		res = remoteDataCollection.notifyMessage(null, msg0506());
		Assert.assertFalse(res.isVoid());

		// controle reponse
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0516");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void testSignOff() throws ISOException {
		// droit de parole ?
		IResponse res = remoteDataCollection.notifyMessage(null, msg0844("851"));
		Assert.assertFalse(res.isVoid());

		// controle reponse
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0854");
			Assert.assertEquals(msg.getString(39), "1000");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}

		// notif deco
		res = remoteDataCollection.notifyMessage(null, msg0844("860"));
		Assert.assertFalse(res.isVoid());

		// controle reponse
		try {
			ISOMsg msg = ISO8583Tools.read_CB2A_TLC(((DataResponse) res).getData());
			Assert.assertEquals(msg.getMTI(), "0854");
			Assert.assertEquals(msg.getString(39), "0000");
		}
		catch (ISO8583Exception | ISOException e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	private String msg0306() throws ISOException {
		ISOMsg isoMsg = new ISO8583Tools().create_CB2A_TLC();
		isoMsg.setMTI("0306");
		isoMsg.set(11, "000011"); // STAN
		isoMsg.set(12, "151925"); // Heure hhmmss
		isoMsg.set(13, "1025"); // Date MMJJ
		isoMsg.set(18, "4511"); // Merchant Category Code
		isoMsg.set(26, "400001"); // Controle transfert
		isoMsg.set(49, "978"); // Currency code
		isoMsg.set(70, "00000100000399");
		return new String(isoMsg.pack());
	}

	private String msg0804() throws ISOException {
		ISOMsg isoMsg = new ISO8583Tools().create_CB2A_TLC();
		isoMsg.setMTI("0804");
		isoMsg.set(11, "000009"); // STAN
		isoMsg.set(12, "181755"); // Heure hhmmss
		isoMsg.set(13, "1017"); // Date MMJJ
		isoMsg.set(24, "862"); // Code Fonction => Dialogue + TLC
		isoMsg.set(25, "10"); // Code Raison => "Programmé par Acquéreur"
		isoMsg.set(32, "51362500080"); // ID Acquéreur (BIN n6 + Banque n5)
		isoMsg.set(41, "FB098F09"); // ID Syst. Acceptation Délivré
		isoMsg.set(42, "316492580FA15BB"); // ID Accepteur carte délivré
		isoMsg.set(67, "0500"); // Gestion des transferts
		return new String(isoMsg.pack());
	}

	private String msg0844(String fonction_code) throws ISOException {
		ISOMsg isoMsg = new ISO8583Tools().create_CB2A_TLC();
		isoMsg.setMTI("0844");
		isoMsg.set(24, fonction_code);
		return new String(isoMsg.pack());
	}

	private String msg0246() throws ISOException {
		ISOMsg isoMsg = ISO8583Tools.create_CB2A_TLC();
		isoMsg.setMTI("0246");
		isoMsg.set(2, "4976710025642130"); // PAN
		isoMsg.set(3, "00"); // Code traitement : Achat bien ou services
		isoMsg.set(4, "8000"); // Montant
		isoMsg.set(11, "000013"); // STAN
		isoMsg.set(12, "173026"); // Heure transac
		isoMsg.set(13, "1008"); // Date transac
		isoMsg.set(22, "105111"); // Condition de réalisation de vente
		isoMsg.set(26, "000002"); // Controle du transfert
		return new String(isoMsg.pack());
	}

	private String msg0506() throws ISOException {
		ISOMsg isoMsg = ISO8583Tools.create_CB2A_TLC();
		isoMsg.setMTI("0506");
		isoMsg.set(12, "102934"); // Heure transac
		isoMsg.set(13, "1009"); // Date transac
		isoMsg.set(70, "00000200000199"); // Gestion du transfert
		isoMsg.set(74, "0000000003"); // nb crédit
		isoMsg.set(76, "0000000000"); // nb débit
		isoMsg.set(77, "0000000000"); // nb débit annulées
		isoMsg.set(86, "0000000000008000"); // Montant total crédit
		isoMsg.set(88, "0000000000000000"); // Montant total débit
		isoMsg.set(89, "0000000000000000"); // Montant total débit annulées
		return new String(isoMsg.pack());
	}
	// /**
	// * generation des messages du EPT
	// *
	// * @throws ISOException
	// */
	// public static void generateMsg() throws ISOException {
	// authorizationRequest = ISO8583Tools.create();
	// authorizationRequest.setMTI("0100");
	// authorizationRequest.set(2, "0123456789123456"); // PAN
	// authorizationRequest.set(3, "000101"); // Type of Auth +
	// // accounts
	// authorizationRequest.set(4, "100"); // 100€
	// authorizationRequest.set(7,
	// ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
	// // :
	// // MMDDhhmmss
	// authorizationRequest.set(38, "123456"); // Approval Code
	// authorizationRequest.set(42, "623598"); // Acceptor's ID
	// authorizationRequest.set(123, "21151168"); // POS Data Code
	// }

}
