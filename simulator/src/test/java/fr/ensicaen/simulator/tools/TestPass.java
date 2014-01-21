package fr.ensicaen.simulator.tools;

import org.junit.Assert;

public class TestPass {

	private static boolean pass = false;

	public static void init() {
		pass = false;
	}

	public static void passed() {
		pass = true;
	}

	public static void assertTest() {
		Assert.assertTrue(pass);
	}
}
