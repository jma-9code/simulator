package ep.strategies.ept;

import java.util.Date;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;
import simulator.SimulatorFactory;
import simulator.exception.SimulatorException;
import tools.ISO7816;
import utils.ISO7816Tools;
import ep.strategies.card.CardTest;

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
	private static ComponentIO fakeSmartCard;

	private static ComponentIO ept;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;
	private static ComponentIO frontOffice;

	@Before
	public void setUp() throws Exception {
		MediatorFactory factory = MediatorFactory.getInstance();

		Context.getInstance().autoRegistrationMode();
		fakeSmartCard = new ComponentIO("Smart Card");

		ept = new ComponentIO("Electronic Payment Terminal");
		ept.setStrategy(new EPTStrategy());

		frontOffice = new ComponentIO("frontOffice");

		smartCardReader = new ComponentIO("Smart Card Reader");
		smartCardReader.setStrategy(new EPTSmartCardReader());
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
		factory.getMediator(smartCardReader, chipset, EMediator.HALFDUPLEX);
		factory.getMediator(smartCardReader, fakeSmartCard, EMediator.HALFDUPLEX);
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
		log.debug("----TEST EPT<->CARD----");
		final String card_sc = "01100060000000000POS ID010000062359800000000PROTOCOL007ISO7816000000000000STAN0060000020000RET REF NUMB01232001200000100000000DATETIME01011212050510000000000000PAN0164976710025642130";
		final String card_ch = "03100090000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT010000000800000CARD AGREEMENT0011PIN VERIFICATION00110000000000000PAN0164976710025642130000000000000STAN0060000040000RET REF NUMB01232001200000100000000DATETIME0101121205051";
		final String card_finalagree = "05000090000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT0100000008000000APPROVAL CODE00607B56=000RESPONSE CODE002000000000000000PAN0164976710025642130000000000000STAN0060000060000RET REF NUMB01232001200000100000000DATETIME0101121205051";

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
							res = ISO7816
									.compareIso7816(
											"01010060000000000POS ID0100000623598000PROTOCOL LIST022ISO7816 ISO8583 CB2A-T0000000PREFERRED007ISO78160000RET REF NUMB012320012000001000000000000STAN00600000100000000DATETIME0101121205051",
											data, ISO7816Tools.FIELD_POSID, ISO7816Tools.FIELD_PROTOCOLLIST,
											ISO7816Tools.FIELD_PROTOCOLPREFERRED, ISO7816Tools.FIELD_STAN);
							Assert.assertTrue(res);

							return DataResponse.build(mediator, card_sc);
						case 1:
							log.debug("card receiv card holder");
							res = ISO7816
									.compareIso7816(
											"03010070000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT010000000800000000000PIN DATA00412340000RET REF NUMB012320012000001000000000000STAN00600000300000000DATETIME0101121205051",
											data, ISO7816Tools.FIELD_POSID, ISO7816Tools.FIELD_OPCODE,
											ISO7816Tools.FIELD_AMOUNT, ISO7816Tools.FIELD_PINDATA,
											ISO7816Tools.FIELD_STAN);
							Assert.assertTrue(res);
							return DataResponse.build(mediator, card_ch);
						case 2:
							log.debug("card receiv final agreement");
							res = ISO7816
									.compareIso7816(
											"04110090000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT0100000008000000APPROVAL CODE00607B56=000RESPONSE CODE002000000000000000PAN0164976710025642130000000000000STAN0060000050000RET REF NUMB01520133480100000500000000DATETIME0101121205051",
											data, ISO7816Tools.FIELD_POSID, ISO7816Tools.FIELD_OPCODE,
											ISO7816Tools.FIELD_APPROVALCODE, ISO7816Tools.FIELD_RESPONSECODE,
											ISO7816Tools.FIELD_PAN, ISO7816Tools.FIELD_STAN);
							Assert.assertTrue(res);
							return DataResponse.build(mediator, card_finalagree);
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
