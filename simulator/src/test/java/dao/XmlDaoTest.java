package dao;

import java.util.Calendar;

import model.component.Component;
import model.component.ComponentIO;
import model.dao.DAO;
import model.dao.ScenarioData;
import model.dao.factory.DAOFactory;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.strategies.NullStrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import simulator.Context;
import tools.Config;

public class XmlDaoTest {
	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;
	private static Context ctx;
	private static MediatorFactory factory = MediatorFactory.getInstance();

	private static Mediator m_ept_card;
	private static Mediator m_card_chip;
	private static Mediator m_card_magstrippe;

	@Before
	public void setUp() throws Exception {
		ctx = Context.getInstance();
		ctx.reset();
		ctx.autoRegistrationMode();

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
		card.getChilds().add(chip);
		chip.getChilds().add(magstrippe);

		card.setStrategy(new NullStrategy());

		m_card_chip = MediatorFactory.getInstance().getMediator(card, chip, EMediator.HALFDUPLEX);
		m_card_magstrippe = MediatorFactory.getInstance().getMediator(card, magstrippe, EMediator.HALFDUPLEX);

		ctx.addStartPoint(Calendar.getInstance().getTime(), "TEST");
		ctx.subscribeEvent(card, "TEST");
	}

	@Test
	public void test_componentXml() {
		DAO<Component> dao = DAOFactory.getFactory().getComponentDAO();
		dao.create(card);
		Component c = dao.find(card.getUuid());
		Assert.assertEquals(card.getName(), c.getName());

		String path = Config.getProps().getProperty("config.xml.path.library.model");
		// a commenter, si on veut voir le xml obtenu (library/model/name_uid)
		// Paths.get(path, card.getName() + "_" +
		// card.getUuid()).toFile().delete();
	}

	@Test
	public void test_scenarioXml() {
		DAO<ScenarioData> dao = DAOFactory.getFactory().getScenarioDataDAO();
		ScenarioData data = new ScenarioData("test", ctx);
		dao.create(data);
		Assert.assertEquals(data.getName(), dao.find(data.getName()).getName());
		String path = Config.getProps().getProperty("config.xml.path.library.scenario");
		// a commenter, si on veut voir le xml obtenu (library/model/name_uid)
		/*
		 * Paths.get(path, card.getName() + "_" +
		 * card.getUuid()).toFile().delete();
		 */
	}
}
