package ep.strategies.ept;

import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ep.strategies.card.CardTest;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.SimulatorFactory;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTChipsetStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTSmartCardReaderStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.ept.EPTStrategy;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools.MessageType;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

/**
 * Electronic Payment Terminal (EPT)
 * 
 * Terminal de Paiement Electronique |- Chipset |- Lecteur carte à piste |-
 * Lecteur carte à puce |- Lecteur carte NFC [*] |- Emplacement SAM |-
 * Emplacement SIM [*] |- Imprimante |- Pinpad sécurisé |- Interface réseau
 * acquéreur (Modem, Ethernet, GPRS, ...) |- Interface caisse (Serial, Ethernet,
 * ...) [*]
 * 
 * [*] : facultatif
 * 
 * Source : http://fr.wikipedia.org/wiki/Terminal_de_paiement_%C3%A9lectronique
 * 
 * @author Flo
 */
public class EPTUnitTest {
	private static Logger log = LoggerFactory.getLogger(CardTest.class);

	private static ISOMsg card_sc = ISO7816Tools.create();
	private static ISOMsg card_ch = ISO7816Tools.create();
	private static ISOMsg card_finalagree = ISO7816Tools.create();
	private static ISOMsg fo_authrp = ISO8583Tools.create();

	private static ComponentIO ept;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;
	private static ComponentIO frontOffice;
	private static ComponentIO fakeSmartCard;

	@Before
	public void setUp() throws Exception {
		MediatorFactory factory = MediatorFactory.getInstance();

		Context.getInstance().autoRegistrationMode();
		fakeSmartCard = new ComponentIO("Smart Card");

		ept = new ComponentIO("Electronic Payment Terminal");
		ept.setStrategy(new EPTStrategy());

		frontOffice = new ComponentIO("Front Office");

		smartCardReader = new ComponentIO("Smart Card Reader");
		smartCardReader.setStrategy(new EPTSmartCardReaderStrategy());
		ept.getChilds().add(smartCardReader);
		factory.getMediator(ept, smartCardReader, EMediator.HALFDUPLEX);

		chipset = new ComponentIO("Chipset");
		chipset.setStrategy(new EPTChipsetStrategy());
		chipset.getProperties().put("pos_id", "0000623598");
		chipset.getProperties().put("stan", "000001");
		chipset.getProperties().put("protocol_list", "ISO7816 ISO8583 CB2A-T");
		chipset.getProperties().put("protocol_prefered", "ISO7816");
		ept.getChilds().add(chipset);
		factory.getMediator(ept, chipset, EMediator.HALFDUPLEX);

		printer = new ComponentIO("Printer");
		ept.getChilds().add(printer);

		securePinPad = new ComponentIO("Secure pin pad");
		ept.getChilds().add(securePinPad);

		networkInterface = new ComponentIO("Network interface");
		ept.getChilds().add(networkInterface);

		// static mediators
		factory.getMediator(ept, frontOffice, EMediator.HALFDUPLEX);
		factory.getMediator(chipset, frontOffice, EMediator.HALFDUPLEX);
		factory.getMediator(smartCardReader, chipset, EMediator.HALFDUPLEX);
		factory.getMediator(smartCardReader, fakeSmartCard, EMediator.HALFDUPLEX);
		generateMsg();
	}

	public void generateMsg() throws ISOException {
		card_sc.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RP));
		card_sc.set(ISO7816Tools.FIELD_POSID, "0000623598");
		card_sc.set(ISO7816Tools.FIELD_PROTOCOL, "ISO7816");
		card_sc.set(ISO7816Tools.FIELD_PAN, "4976710025642130");
		card_sc.set(ISO7816Tools.FIELD_RRN, "320012000001");
		card_sc.set(ISO7816Tools.FIELD_STAN, "000002");
		card_sc.set(ISO7816Tools.FIELD_DATETIME, "1008170100");

		card_ch.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RP));
		card_ch.set(ISO7816Tools.FIELD_POSID, "0000623598");
		card_ch.set(ISO7816Tools.FIELD_OPCODE, "00");
		card_ch.set(ISO7816Tools.FIELD_AMOUNT, "0000008000");
		card_ch.set(ISO7816Tools.FIELD_PINVERIFICATION, "1");
		card_ch.set(ISO7816Tools.FIELD_CARDAGREEMENT, "1");
		card_sc.set(ISO7816Tools.FIELD_PAN, "4976710025642130");
		card_ch.set(ISO7816Tools.FIELD_RRN, "320012000001");
		card_ch.set(ISO7816Tools.FIELD_STAN, "000004");
		card_ch.set(ISO7816Tools.FIELD_DATETIME, "1008170100");

		card_finalagree.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.TRANSCATION_VAL_NOTIF));
		card_finalagree.set(ISO7816Tools.FIELD_POSID, "0000623598");
		card_finalagree.set(ISO7816Tools.FIELD_OPCODE, "00");
		card_finalagree.set(ISO7816Tools.FIELD_AMOUNT, "0000008000");
		card_finalagree.set(ISO7816Tools.FIELD_APPROVALCODE, "07B56=");
		card_finalagree.set(ISO7816Tools.FIELD_RESPONSECODE, "00");
		card_finalagree.set(ISO7816Tools.FIELD_PAN, "4976710025642130");
		card_finalagree.set(ISO7816Tools.FIELD_RRN, "320012000001");
		card_finalagree.set(ISO7816Tools.FIELD_STAN, "000001");
		card_finalagree.set(ISO7816Tools.FIELD_DATETIME, "1008170100");

		fo_authrp.setMTI("0110");
		fo_authrp.set(7, "1008170100");
		fo_authrp.set(39, "00");
		// @TODO
		// fo_authrp.set(c);
		// fo_authrp.set(c);
		// fo_authrp.set(c);
		// fo_authrp.set(c);
		//
		// String amount = data.getString(ISO7816Tools.FIELD_AMOUNT);
		// String apcode = data.getString(ISO7816Tools.FIELD_APPROVALCODE);
		// String rescode = data.getString(ISO7816Tools.FIELD_RESPONSECODE);
		// String pan = data.getString(ISO7816Tools.FIELD_PAN);
		// String stan = data.getString(ISO7816Tools.FIELD_STAN);
	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void testPipedMediator() throws SimulatorException {
		fakeSmartCard.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				return DataResponse.build(mediator, "TEST ANSWER CARD");
			}

		});
		// chipset.notifyEvent("SMART_CARD_INSERTED");

		// add start point for the simulator
		Context.getInstance().addStartPoint(new Date(), "SMART_CARD_INSERTED");

		// execute simulation.
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void card_tpetest() {
		log.debug("----TEST FO<->EPT<->CARD----");
		fakeSmartCard.setStrategy(new IStrategy<ComponentIO>() {
			private int msg = -1;

			@Override
			public void processEvent(ComponentIO _this, String event) {

			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				try {
					boolean res = false;
					msg++;
					switch (msg) {
						case 0:
							log.debug("card receiv secure channel");
							res = true;
							Assert.assertTrue(res);
							return DataResponse.build(mediator, new String(card_sc.pack()));
						case 1:
							log.debug("card receiv card holder");
							res = true;
							Assert.assertTrue(res);
							return DataResponse.build(mediator, new String(card_ch.pack()));
						case 2:
							log.debug("card receiv final agreement");
							res = true;
							Assert.assertTrue(res);
							return DataResponse.build(mediator, new String(card_finalagree.pack()));
						default:
							break;
					}
				}
				catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.assertFalse(true);
				}

				return VoidResponse.build();
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "SMART_CARD_INSERTED");
			}

		});

		frontOffice.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
				// TODO Auto-generated method stub

			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				// TODO Auto-generated method stub

			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				Assert.assertTrue(true);
				try {
					return DataResponse.build(mediator, new String(fo_authrp.pack()));
				}
				catch (ISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});

		// on insert la carte dans le tpe, le tpe envoie des donnees a la carte
		Context.getInstance().addStartPoint(new Date(), "SMART_CARD_INSERTED");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		log.debug("----TEST EPT<->CARD END----");
	}
}
