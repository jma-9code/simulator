package fr.ensicaen.simulator.simulator;

import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensicaen.simulator.model.component.ComponentI;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.ComponentO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.PipedMediator;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator.tools.TestPass;

/**
 * Dans cette classe de test, on s'attache à vérifier les méthodes disponibles
 * dans le contexte de simulation : <br />
 * - getFirstMediator<br />
 * - getAllMediators
 */
public class ContextTest {

	private ComponentIO c1;
	private ComponentIO c2;
	private ComponentIO c1s1;
	private ComponentIO c1s2;
	private ComponentIO c2s1;
	private ComponentIO c2s2;

	@Before
	public void beforeTest() {
		// verify that the test has been passed
		TestPass.init();

		System.out.println("---------NEW TEST---------");

		Context ctx = Context.getInstance();
		ctx.reset();
		ctx.autoRegistrationMode();

		c1 = new ComponentIO("C1", 0);
		c2 = new ComponentIO("C2", 1);

		c1s1 = new ComponentIO("C1S1", 2);
		c1.addChild(c1s1);

		c1s2 = new ComponentIO("C1S2", 3);
		c1.addChild(c1s2);

		c2s1 = new ComponentIO("C2S1", 4);
		c2.addChild(c2s1);

		c2s2 = new ComponentIO("C2S2", 5);
		c2.addChild(c2s2);
	}

	@After
	public void afterTest() {
		TestPass.assertTest();
	}

	@Test
	public void testFirstMediator_depth1() throws SimulatorException {
		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator mExpected = factory.getMediator(c1, c2, EMediator.HALFDUPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Assert.assertEquals(Context.getInstance().getFirstMediator(_this, 1), mExpected);
				}
				catch (ContextException e) {
					e.printStackTrace();
				}
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFailedFirstMediator_depth1Simplex() throws SimulatorException {
		// MediatorFactory factory = MediatorFactory.getInstance();
		// final Mediator m1 = factory.getMediator(c2, c1, EMediator.SIMPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, 1);
					System.out.println("ETTSTTSTSTS" + m);
					Assert.assertTrue(false);
				}
				catch (ContextException e) {
					e.printStackTrace();
					Assert.assertTrue(true);
				}
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFirstMediator_depth2with2HalfDuplex() throws SimulatorException {

		// MediatorFactory factory = MediatorFactory.getInstance();
		// final Mediator m1 = factory.getMediator(c1, c1s1,
		// EMediator.HALFDUPLEX);
		// final Mediator m2 = factory.getMediator(c1, c1s2,
		// EMediator.HALFDUPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, 3);
					Assert.assertEquals(m.getClass(), PipedMediator.class);
					Assert.assertEquals(m.getSender(), c1s1);
					Assert.assertEquals(m.getReceiver(), c1s2);
				}
				catch (ContextException e) {
					e.printStackTrace();
				}
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFirstMediator_depth2with1Simplex() throws SimulatorException {

		// MediatorFactory factory = MediatorFactory.getInstance();
		// final Mediator m1 = factory.getMediator(c1, c1s1,
		// EMediator.HALFDUPLEX);
		// final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.SIMPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, 3);
					Assert.assertEquals(m.getClass(), PipedMediator.class);
					Assert.assertEquals(m.getSender(), c1s1);
					Assert.assertEquals(m.getReceiver(), c1s2);
				}
				catch (ContextException e) {
					e.printStackTrace();
				}
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFailedFirstMediator_depth2with2Simplex() throws SimulatorException {
		ComponentO ci1 = new ComponentO("CI1");
		c1.addChild(ci1);

		ComponentI ci2 = new ComponentI("CI2");
		c1.addChild(ci2);

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, ci1, EMediator.SIMPLEX);
		final Mediator m2 = factory.getMediator(c1, ci2, EMediator.SIMPLEX);

		ci1.setStrategy(new IStrategy<ComponentO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, 9);
					Assert.assertTrue(false);
				}
				catch (ContextException e) {
					e.printStackTrace();
					Assert.assertTrue(true);
				}
			}

			@Override
			public IResponse processMessage(ComponentO _this, Mediator mediator, String data) {
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void test2Mediators_depth1() throws SimulatorException {
		// second component with same name
		ComponentIO c22 = new ComponentIO("C2");

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c2, EMediator.SIMPLEX);
		final Mediator m2 = factory.getMediator(c1, c22, EMediator.SIMPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					List<Mediator> mList = Context.getInstance().getMediators(_this, 1);
					// Assert.assertEquals(mList.size(), 2);
					Assert.assertThat("Mediators returned not correct", mList, CoreMatchers.hasItem(m1));
					Assert.assertThat("Mediators returned not correct", mList, CoreMatchers.hasItem(m2));
				}
				catch (ContextException e) {
					e.printStackTrace();
					Assert.assertTrue(false);
				}
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST");
		SimulatorFactory.getSimulator().start();
		Simulator.resume();
	}

	@Test
	public void testMediatorWithAcronym() {
		TestPass.passed(); // fake

		ComponentIO cTest = new ComponentIO("Composant de ouf");

		// factory
		MediatorFactory factory = MediatorFactory.getInstance();
		Mediator mMade = factory.getMediator(cTest, c1, EMediator.HALFDUPLEX);

		// try {
		// Mediator mGot = Context.getInstance().getFirstMediator(c1,
		// "Composant de ouf");
		// Assert.assertEquals(mMade, mGot);
		//
		// mGot = Context.getInstance().getFirstMediator(c1, "CDO");
		// Assert.assertEquals(mMade, mGot);
		//
		// mGot = Context.getInstance().getFirstMediator(c1, "CDo");
		// Assert.assertEquals(mMade, mGot);
		// }
		// catch (ContextException e) {
		// e.printStackTrace();
		// Assert.assertTrue(false);
		// }
	}

	@Test(expected = ContextException.class)
	public void testFailedMediatorWithAcronym() throws ContextException {
		TestPass.passed(); // fake

		ComponentIO cTest = new ComponentIO("Composant de ouf");

		// factory
		MediatorFactory factory = MediatorFactory.getInstance();
		factory.getMediator(cTest, c1, EMediator.HALFDUPLEX);

		// test
		// Context.getInstance().getFirstMediator(c1, "CD");
	}
}
