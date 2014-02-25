package ep.strategies.network;

import java.util.Calendar;
import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
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
import fr.ensicaen.simulator.tools.TestPass;
import fr.ensicaen.simulator_ep.ep.strategies.network.GenericNetworkStrategy;
import fr.ensicaen.simulator_ep.ep.strategies.network.GenericRouterStrategy;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;

public class GenericRouterUnitTest {

	private static Logger log = LoggerFactory.getLogger(GenericRouterUnitTest.class);
	private static MediatorFactory factory = MediatorFactory.getInstance();

	/** Test launcher */
	private static ComponentIO testLauncher;
	/** Router (component tested) */
	private static ComponentIO router;
	/** Network eRSB */
	private static ComponentIO eRSBNetwork;
	/** Network Visanet */
	private static ComponentIO visaNetNetwork;
	/** Network Banknet */
	private static ComponentIO bankNetNetwork;

	private static Mediator m_test_router;
	private static Mediator m_router_ersb;
	private static Mediator m_router_banknet;
	private static Mediator m_router_visanet;

	private final static String networkId1 = "ersb";
	private final static String networkId2 = "visanet";
	private final static String networkId3 = "banknet";

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();

		testLauncher = new ComponentIO("Test Launcher");

		router = new ComponentIO("Router");
		router.setStrategy(new GenericRouterStrategy());

		eRSBNetwork = new ComponentIO("Network");
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEY_NAME, "e-RSB");

		visaNetNetwork = new ComponentIO("Network");
		visaNetNetwork.getProperties().put(GenericNetworkStrategy.CKEY_NAME, "VisaNet");

		bankNetNetwork = new ComponentIO("Network");
		bankNetNetwork.getProperties().put(GenericNetworkStrategy.CKEY_NAME, "BankNet");

		m_test_router = MediatorFactory.getInstance().getMediator(testLauncher, router, EMediator.HALFDUPLEX);
		m_router_ersb = MediatorFactory.getInstance().getMediator(router, eRSBNetwork, EMediator.HALFDUPLEX);
		m_router_visanet = MediatorFactory.getInstance().getMediator(router, visaNetNetwork, EMediator.HALFDUPLEX);
		m_router_banknet = MediatorFactory.getInstance().getMediator(router, bankNetNetwork, EMediator.HALFDUPLEX);
	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void ersbRouteTest() {
		TestPass.init();

		// config route iin => network
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "497", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "513", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "4", networkId2);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "5", networkId3);

		// config identification mediator/network
		m_router_ersb.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId1);
		m_router_visanet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId2);
		m_router_banknet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId3);

		eRSBNetwork.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				TestPass.passed();
				return DataResponse.build(mediator, data);
			}

		});

		testLauncher.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				return VoidResponse.build();
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				// auth request test
				ISOMsg authorizationRequest = new ISOMsg();
				try {

					authorizationRequest.setPackager(new GenericPackager(getClass().getResource("/8583.xml")
							.toExternalForm()));
					authorizationRequest.setMTI("0100");
					authorizationRequest.set(2, "4970210000000000"); // PAN
					authorizationRequest.set(4, "10000"); // 100€
					authorizationRequest.set(7, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
																												// //
																												// MMDDhhmmss
				}
				catch (ISOException e) {
					e.printStackTrace();
				}

				// send to network
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Router");
					m.send(_this, new String(authorizationRequest.pack()));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_ROUTING");
			}

		});

		Context.getInstance().addStartPoint(new Date(), "TEST_ROUTING");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}

		TestPass.assertTest();
	}

	@Test
	public void visaRouteTest() {
		TestPass.init();

		// config route iin => network
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "497", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "513", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "4", networkId2);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "5", networkId3);

		// config identification mediator/network
		m_router_ersb.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId1);
		m_router_visanet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId2);
		m_router_banknet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId3);

		visaNetNetwork.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				TestPass.passed();
				return DataResponse.build(mediator, data);
			}

		});

		testLauncher.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				return VoidResponse.build();
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				// auth request test
				ISOMsg authorizationRequest = new ISOMsg();
				try {
					authorizationRequest.setPackager(new GenericPackager(getClass().getResource("/8583.xml")
							.toExternalForm()));
					authorizationRequest.setMTI("0100");
					authorizationRequest.set(2, "4670210000000000"); // PAN
					authorizationRequest.set(4, "10000"); // 100€
					authorizationRequest.set(7, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
																												// //
																												// MMDDhhmmss
				}
				catch (ISOException e) {
					e.printStackTrace();
				}

				// send to network
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Router");
					DataResponse res = (DataResponse) m.send(_this, new String(authorizationRequest.pack()));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_ROUTING");
			}

		});

		Context.getInstance().addStartPoint(new Date(), "TEST_ROUTING");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}

		TestPass.assertTest();
	}

	@Test
	public void mcRouteTest() {
		TestPass.init();

		// config route iin => network
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "497", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "513", networkId1);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "4", networkId2);
		router.getProperties().put(GenericRouterStrategy.CKEYPREFIX_NETWORK_OF + "5", networkId3);

		// config identification mediator/network
		m_router_ersb.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId1);
		m_router_visanet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId2);
		m_router_banknet.getProperties().put(GenericRouterStrategy.MKEY_NETWORK_ID, networkId3);

		bankNetNetwork.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				TestPass.passed();
				return DataResponse.build(mediator, data);
			}

		});

		testLauncher.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				return VoidResponse.build();
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
				// auth request test
				ISOMsg authorizationRequest = new ISOMsg();
				try {

					authorizationRequest.setPackager(new GenericPackager(getClass().getResource("/8583.xml")
							.toExternalForm()));
					authorizationRequest.setMTI("0100");
					authorizationRequest.set(2, "5670210000000000"); // PAN
					authorizationRequest.set(4, "10000"); // 100€
					authorizationRequest.set(7, ISO7816Tools.writeDATETIME(Calendar.getInstance().getTime())); // date
																												// //
																												// MMDDhhmmss
				}
				catch (ISOException e) {
					e.printStackTrace();
				}

				// send to network
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Router");
					DataResponse res = (DataResponse) m.send(_this, new String(authorizationRequest.pack()));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_ROUTING");
			}

		});

		Context.getInstance().addStartPoint(new Date(), "TEST_ROUTING");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}

		TestPass.assertTest();
	}
}
