package card;

import java.util.Date;

import model.component.ComponentIO;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simulator.Context;
import simulator.SimulatorFactory;
import simulator.exception.SimulatorException;
import ep.strategies.card.CardChipStrategy;
import ep.strategies.card.CardStrategy;
import ep.strategies.ept.EPTChipsetStrategy;
import ep.strategies.ept.EPTSmartCardReader;
import ep.strategies.ept.EPTStrategy;

public class CardTest {

	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;
	private static ComponentIO ept;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;
	private static ComponentIO fakeSmartCard;
	private static MediatorFactory factory = MediatorFactory.getInstance();

	private static Mediator m_smartReader_card;
	private static Mediator m_card_chip;
	private static Mediator m_card_magstrippe;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// fake tpe
		fakeSmartCard = new ComponentIO("Smart Card");

		ept = new ComponentIO("Electronic Payment Terminal");
		ept.setStrategy(new EPTStrategy());

		smartCardReader = new ComponentIO("Smart Card Reader");
		smartCardReader.setStrategy(new EPTSmartCardReader());
		ept.getComponents().add(smartCardReader);
		factory.getMediator(ept, smartCardReader, EMediator.HALFDUPLEX);

		chipset = new ComponentIO("Chipset");
		chipset.setStrategy(new EPTChipsetStrategy());
		chipset.getProperties().put("pos_id", "0000623598");
		chipset.getProperties().put("stan", "000001");
		chipset.getProperties().put("protocol_list", "ISO7816 ISO8583 CB2A-T");
		chipset.getProperties().put("protocol_prefered", "ISO7816");
		ept.getComponents().add(chipset);
		factory.getMediator(ept, chipset, EMediator.HALFDUPLEX);

		printer = new ComponentIO("Printer");
		ept.getComponents().add(printer);

		securePinPad = new ComponentIO("Secure pin pad");
		ept.getComponents().add(securePinPad);

		networkInterface = new ComponentIO("Network interface");
		ept.getComponents().add(networkInterface);

		card = new ComponentIO("cb");

		card.getProperties().put("pan", "1111111111111111111111111");
		card.getProperties().put("icvv", "000");
		card.getProperties().put("type", "M");
		card.getProperties().put("name", "Florent Moisson");
		card.getProperties().put("date expiration", "09/15");

		chip = new ComponentIO("chip");
		chip.getProperties().put("protocol", "ISO7816");
		chip.getProperties().put("pan", "1111111111111111111111111");
		chip.getProperties().put("bccs", "12421874");
		chip.getProperties().put("ceil", "400");
		chip.getProperties().put("approvalcode", "07B56=");
		chip.getProperties().put("state", "OFF");

		magstrippe = new ComponentIO("magstrippe");
		magstrippe.getProperties().put("iso2", "59859595985888648468454684");

		card.getComponents().add(magstrippe);
		card.getComponents().add(chip);

		card.setStrategy(new CardStrategy());
		chip.setStrategy(new CardChipStrategy());
		m_smartReader_card = MediatorFactory.getInstance().getMediator(card, smartCardReader, EMediator.HALFDUPLEX);
		m_card_chip = MediatorFactory.getInstance().getMediator(card, chip, EMediator.HALFDUPLEX);
		m_card_magstrippe = MediatorFactory.getInstance().getMediator(card, magstrippe, EMediator.HALFDUPLEX);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void SecureChanneltest() {

		// TPE SEND info for init sc
		String tpe_sc = "01010040000000000POS ID0100000623598000PROTOCOL LIST022ISO7816 ISO8583 CB2A-T0000000PREFERRED007ISO781600000000DATETIME0101008170100";
		String tpe_auth = "03010050000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT010000000800000000000PIN DATA004123400000000DATETIME0101008170934";
		String tpe_arqc = "04110070000000000POS ID0100000623598000000000OP CODE002000000000000AMOUNT0100000008000000APPROVAL CODE00607B56=000RESPONSE CODE002000000000000000PAN016497671002564213000000000DATETIME0101008173026";

		// on insert la carte dans le tpe, le tpe envoie des donnees a la carte
		Context.getInstance().addStartPoint(new Date(), smartCardReader, "SMART_CARD_INSERTED");
		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
