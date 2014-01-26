package ep.strategies.card;

import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import fr.ensicaen.simulator_ep.ep.strategies.card.CardChipStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.card.CardStrategy;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools.MessageType;

public class CardTest {
	private static Logger log = LoggerFactory.getLogger(CardTest.class);
	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;
	private static ComponentIO ept;
	private static MediatorFactory factory = MediatorFactory.getInstance();

	private static Mediator m_ept_card;
	private static Mediator m_card_chip;
	private static Mediator m_card_magstrippe;

	private static ISOMsg tpe_sc = ISO7816Tools.create();
	private static ISOMsg tpe_ch = ISO7816Tools.create();
	private static ISOMsg tpe_finalagree = ISO7816Tools.create();

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();
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

		ept = new ComponentIO("ept");

		card.setStrategy(new CardStrategy());
		chip.setStrategy(new CardChipStrategy());
		m_ept_card = MediatorFactory.getInstance().getMediator(card, ept, EMediator.HALFDUPLEX);
		m_card_chip = MediatorFactory.getInstance().getMediator(card, chip, EMediator.HALFDUPLEX);
		m_card_magstrippe = MediatorFactory.getInstance().getMediator(card, magstrippe, EMediator.HALFDUPLEX);
		generateTPEMsg();
	}

	public void generateTPEMsg() throws ISOException {
		// MSG GENERER PAR LE TPE
		tpe_sc.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.SECURE_CHANNEL_RQ));
		tpe_sc.set(ISO7816Tools.FIELD_POSID, "0000623598");
		tpe_sc.set(ISO7816Tools.FIELD_PROTOCOLLIST, "ISO7816 ISO8583 CB2A-T");
		tpe_sc.set(ISO7816Tools.FIELD_PROTOCOLPREFERRED, "ISO7816");
		tpe_sc.set(ISO7816Tools.FIELD_RRN, "320012000001");
		tpe_sc.set(ISO7816Tools.FIELD_STAN, "000001");
		tpe_sc.set(ISO7816Tools.FIELD_DATETIME, "1008170100");

		tpe_ch.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.CARDHOLDER_AUTH_RQ));
		tpe_ch.set(ISO7816Tools.FIELD_POSID, "0000623598");
		tpe_ch.set(ISO7816Tools.FIELD_OPCODE, "00");
		tpe_ch.set(ISO7816Tools.FIELD_AMOUNT, "0000008000");
		tpe_ch.set(ISO7816Tools.FIELD_PINDATA, "1234");
		tpe_ch.set(ISO7816Tools.FIELD_RRN, "320012000001");
		tpe_ch.set(ISO7816Tools.FIELD_STAN, "000001");
		tpe_ch.set(ISO7816Tools.FIELD_DATETIME, "1008170100");

		tpe_finalagree.setMTI(ISO7816Tools.convertType2CodeMsg(MessageType.AUTHORIZATION_RP_CRYPTO));
		tpe_finalagree.set(ISO7816Tools.FIELD_POSID, "0000623598");// num du tpe
		tpe_finalagree.set(ISO7816Tools.FIELD_OPCODE, "00"); //
		tpe_finalagree.set(ISO7816Tools.FIELD_AMOUNT, "0000008000");
		tpe_finalagree.set(ISO7816Tools.FIELD_APPROVALCODE, "07B56="); // num
																		// d'auth
		tpe_finalagree.set(ISO7816Tools.FIELD_RESPONSECODE, "00"); // auth OK
		tpe_finalagree.set(ISO7816Tools.FIELD_PAN, "4976710025642130");
		tpe_finalagree.set(ISO7816Tools.FIELD_RRN, "320012000001");
		tpe_finalagree.set(ISO7816Tools.FIELD_STAN, "000001");
		tpe_finalagree.set(ISO7816Tools.FIELD_DATETIME, "1008170100");
	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void cardtest() throws ISOException {
		log.info("----TEST CARD----");

		ept.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void processEvent(ComponentIO _this, String event) {
				try {
					log.info("tpe send secure channel");
					DataResponse rp = (DataResponse) m_ept_card.send(_this, new String(tpe_sc.pack()));
					log.debug("card rp :" + rp.getData());
					boolean res = true;
					Assert.assertTrue(res);

					log.info("tpe send card holder");
					rp = (DataResponse) m_ept_card.send(_this, new String(tpe_ch.pack()));
					log.debug("card rp :" + rp.getData());
					res = true;
					Assert.assertTrue(res);
					log.info("tpe send final agreement");
					rp = (DataResponse) m_ept_card.send(_this, new String(tpe_finalagree.pack()));
					log.debug("card rp :" + rp.getData());
					res = true;
					Assert.assertTrue(res);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.assertFalse(true);
				}

			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
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
		log.info("----TEST CARD END----");
	}
}
