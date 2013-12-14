package dao;

import java.nio.file.Files;
import java.nio.file.Paths;

import model.component.Component;
import model.component.ComponentIO;
import model.dao.DAO;
import model.dao.factory.DAOFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.Config;

public class XmlDaoTest {
	private static ComponentIO card;
	private static ComponentIO chip;
	private static ComponentIO magstrippe;

	@Before
	public void setUp() throws Exception {
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

		card.getComponents().add(magstrippe);
		card.getComponents().add(chip);
	}

	@Test
	public void test_componentXml() {
		DAO<Component> dao = DAOFactory.getFactory().getComponentDAO();
		dao.create(card);
		String path = Config.getProps().getProperty("config.xml.path.library.model");
		Assert.assertTrue(Files.exists(Paths.get(path, card.getName() + "_" + card.getUuid())));
		// a commenter, si on veut voir le xml obtenu (library/model/name_uid)
		Paths.get(path, card.getName() + "_" + card.getUuid()).toFile().delete();
	}
}
