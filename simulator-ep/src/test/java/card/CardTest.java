package card;

import java.util.Date;

import model.component.ComponentIO;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.strategies.NullStrategy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simulator.Context;
import simulator.SimulatorException;
import simulator.SimulatorFactory;
import ep.strategies.card.CardStrategy;
import ep.strategies.card.ChipStrategy;

public class CardTest {

	private static ComponentIO tpe;
	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;

	private static Mediator m_tpe_card;
	private static Mediator m_card_chip;
	private static Mediator m_card_magstrippe;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// fake tpe
		tpe = new ComponentIO("tpe");

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
		chip.setStrategy(new ChipStrategy());
		tpe.setStrategy(new NullStrategy());

		m_tpe_card = MediatorFactory.getInstance().getMediator(card, tpe, EMediator.HALFDUPLEX);
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
		Context.getInstance().addStartPoint(new Date(), tpe,
				MediatorFactory.getInstance().getMediator(card, tpe, EMediator.HALFDUPLEX), tpe_arqc);
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
