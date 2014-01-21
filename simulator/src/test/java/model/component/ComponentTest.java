package model.component;

import org.junit.Assert;
import org.junit.Test;

public class ComponentTest {

	private static ComponentIO c;

	@Test
	public void testAcronym() {
		c = new ComponentIO("Coucou");
		Assert.assertEquals("C", c.getAcronym());

		c = new ComponentIO("Coucou Toi");
		Assert.assertEquals("CT", c.getAcronym());

		c = new ComponentIO("Coucou  Toi");
		Assert.assertEquals("CT", c.getAcronym());

		c = new ComponentIO("Coucou  Toi ");
		Assert.assertEquals("CT", c.getAcronym());

		c = new ComponentIO("coucou  toi ");
		Assert.assertEquals("CT", c.getAcronym());

		c = new ComponentIO("coucou  toi toi toi");
		Assert.assertEquals("CTTT", c.getAcronym());
	}
}
