package simulator;

import java.util.Date;
import java.util.List;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.mediator.PipedMediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import simulator.exception.ContextException;
import simulator.exception.SimulatorException;
import tools.TestPass;

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

		c1 = new ComponentIO("C1");
		c2 = new ComponentIO("C2");

		c1s1 = new ComponentIO("C1S1");
		c1.getComponents().add(c1s1);

		c1s2 = new ComponentIO("C1S2");
		c1.getComponents().add(c1s2);

		c2s1 = new ComponentIO("C2S1");
		c2.getComponents().add(c2s1);

		c2s2 = new ComponentIO("C2S2");
		c2.getComponents().add(c2s2);
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
					Assert.assertEquals(Context.getInstance().getFirstMediator(_this, "C2"), mExpected);
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
		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c2, c1, EMediator.SIMPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "C2");
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

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.HALFDUPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.HALFDUPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "C1S2");
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

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.HALFDUPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.SIMPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "C1S2");
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

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.SIMPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.SIMPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST");
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				TestPass.passed();

				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "C1S2");
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
					List<Mediator> mList = Context.getInstance().getMediators(_this, "C2");
					Assert.assertEquals(mList.size(), 2);
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
	}
}