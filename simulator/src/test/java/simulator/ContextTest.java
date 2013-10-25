package simulator;

import java.util.Date;

import model.component.ComponentIO;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.mediator.PipedMediator;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import simulator.exception.ContextException;
import simulator.exception.SimulatorException;

public class ContextTest {

	private ComponentIO c1;
	private ComponentIO c2;
	private ComponentIO c1s1;
	private ComponentIO c1s2;
	private ComponentIO c2s1;
	private ComponentIO c2s2;

	@Before
	public void beforeTest() {
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

	@Test
	public void testFirstMediator_depth1() throws SimulatorException {
		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator mExpected = factory.getMediator(c1, c2, EMediator.HALFDUPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
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

		Context.getInstance().addStartPoint(new Date(), c1, "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFailedFirstMediator_depth1Simplex() throws SimulatorException {
		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c2, c1, EMediator.SIMPLEX);

		c1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
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

		Context.getInstance().addStartPoint(new Date(), c1, "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFirstMediator_depth2with2HalfDuplex() throws SimulatorException {

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.HALFDUPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.HALFDUPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
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

		Context.getInstance().addStartPoint(new Date(), c1s1, "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFirstMediator_depth2with1Simplex() throws SimulatorException {

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.HALFDUPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.SIMPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
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

		Context.getInstance().addStartPoint(new Date(), c1s1, "TEST");
		SimulatorFactory.getSimulator().start();
	}

	@Test
	public void testFailedFirstMediator_depth2with2Simplex() throws SimulatorException {

		MediatorFactory factory = MediatorFactory.getInstance();
		final Mediator m1 = factory.getMediator(c1, c1s1, EMediator.SIMPLEX);
		final Mediator m2 = factory.getMediator(c1, c1s2, EMediator.SIMPLEX);

		c1s1.setStrategy(new IStrategy<ComponentIO>() {
			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
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

		Context.getInstance().addStartPoint(new Date(), c1s1, "TEST");
		SimulatorFactory.getSimulator().start();
	}
}
