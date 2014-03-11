package ep.strategies.network;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.SimulatorFactory;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator.tools.TestPass;
import fr.ensicaen.simulator_ep.ep.strategies.network.GenericNetworkStrategy;
import fr.ensicaen.simulator_ep.utils.CB2AValues;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;

public class GenericNetworkUnitTest {

	private static Logger log = LoggerFactory.getLogger(GenericNetworkUnitTest.class);
	private static MediatorFactory factory = MediatorFactory.getInstance();

	/** Test launcher */
	private static ComponentIO testLauncher;
	/** Network (component tested) */
	private static ComponentIO eRSBNetwork;
	/** Issuer Authorization Module 1 */
	private static ComponentIO iam1;
	/** Issuer Authorization Module 2 */
	private static ComponentIO iam2;
	/** Issuer Authorization Module 3 */
	private static ComponentIO iam3;

	private static Mediator m_test_ersb;
	private static Mediator m_ersb_iam1;
	private static Mediator m_ersb_iam2;

	private final static String issuerId1 = "issuerId1112";
	private final static String issuerId2 = "issuerId5455";

	@Before
	public void init() throws Exception {
		Context.getInstance().autoRegistrationMode();

		// mock strategy to launch the test
		testLauncher = new ComponentIO("Test Launcher");

		eRSBNetwork = new ComponentIO("Network");
		eRSBNetwork.setStrategy(new GenericNetworkStrategy());
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEY_NAME, "e-RSB");

		iam1 = new ComponentIO("Issuer Authorization Module");
		// no strategy

		iam2 = new ComponentIO("Issuer Authorization Module");
		// no strategy

		iam3 = new ComponentIO("Issuer Authorization Module");
		// no strategy

		m_test_ersb = MediatorFactory.getInstance().getMediator(testLauncher, eRSBNetwork, EMediator.HALFDUPLEX);
		m_ersb_iam1 = MediatorFactory.getInstance().getMediator(eRSBNetwork, iam1, EMediator.HALFDUPLEX);
		m_ersb_iam2 = MediatorFactory.getInstance().getMediator(eRSBNetwork, iam2, EMediator.HALFDUPLEX);

	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void routeToUnknownIssuerTest() {

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
				}
				catch (ISOException e) {
					e.printStackTrace();
				}

				// send to network
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Network");
					DataResponse res = (DataResponse) m.send(_this, new String(authorizationRequest.pack()));

					// update bean
					authorizationRequest.unpack(res.getData().getBytes());

					// test
					Assert.assertEquals(authorizationRequest.getMTI(), "0110");
					Assert.assertEquals(authorizationRequest.getValue(39), CB2AValues.Field39.UNKNOWN_CARD_ISSUER);

					log.debug("Answer : " + res.getData());
				}
				catch (Exception e) {
					e.printStackTrace();
					Assert.assertTrue(false);
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_NETWORK");
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		Context.getInstance().addStartPoint(new Date(), "TEST_NETWORK");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void routeToUnreachableIssuerTest() {
		// config route iin => issuer
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEYPREFIX_ISSUER_OF + "467021", issuerId1);
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEYPREFIX_ISSUER_OF + "468454", issuerId2);

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
				}
				catch (ISOException e) {
					e.printStackTrace();
					Assert.assertTrue(false);
				}

				// send to network
				try {
					Mediator m = Context.getInstance().getFirstMediator(_this, "Network");
					DataResponse res = (DataResponse) m.send(_this, new String(authorizationRequest.pack()));

					// update bean
					authorizationRequest.unpack(res.getData().getBytes());

					// test
					Assert.assertEquals(authorizationRequest.getMTI(), "0110");
					Assert.assertEquals(authorizationRequest.getValue(39), CB2AValues.Field39.UNREACHABLE_CARD_ISSUER);

					log.debug("Answer : " + res.getData());
				}
				catch (Exception e) {
					e.printStackTrace();
					Assert.assertTrue(false);
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_NETWORK");
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}

		});

		Context.getInstance().addStartPoint(new Date(), "TEST_NETWORK");

		// execute simulation.
		try {
			SimulatorFactory.getSimulator().start();
		}
		catch (SimulatorException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void routeToIssuerTest() {
		TestPass.init();

		// config route iin => issuer
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEYPREFIX_ISSUER_OF + "467021", issuerId1);
		eRSBNetwork.getProperties().put(GenericNetworkStrategy.CKEYPREFIX_ISSUER_OF + "468454", issuerId2);

		// link network to issuer (mediator setup)
		m_ersb_iam1 = MediatorFactory.getInstance().getMediator(eRSBNetwork, iam1, EMediator.HALFDUPLEX);
		m_ersb_iam2 = MediatorFactory.getInstance().getMediator(eRSBNetwork, iam2, EMediator.HALFDUPLEX);

		// config identification mediator/issuer
		m_ersb_iam1.getProperties().put(GenericNetworkStrategy.MKEY_ISSUER_ID, issuerId1);
		m_ersb_iam2.getProperties().put(GenericNetworkStrategy.MKEY_ISSUER_ID, issuerId2);

		iam1.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				// auth request test
				ISOMsg authorizationRequest = new ISOMsg();
				try {

					authorizationRequest.setPackager(new GenericPackager(getClass().getResource("/8583.xml")
							.toExternalForm()));
					authorizationRequest.unpack(data.getBytes());

					authorizationRequest.set(39, CB2AValues.Field39.TRANSACTION_APPROVED);
					authorizationRequest.setResponseMTI();

					TestPass.passed();
					return DataResponse.build(mediator, authorizationRequest.pack());
				}
				catch (ISOException e) {
					e.printStackTrace();
					return DataResponse.build(mediator, data);
				}
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}

		});

		iam2.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				Assert.assertTrue(false);
				return DataResponse.build(mediator, data);
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		iam3.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				Assert.assertTrue(false);
				return DataResponse.build(mediator, data);
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
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
					Mediator m = Context.getInstance().getFirstMediator(_this, "Network");
					DataResponse res = (DataResponse) m.send(_this, new String(authorizationRequest.pack()));

					// update bean
					authorizationRequest.unpack(res.getData().getBytes());

					// test
					Assert.assertEquals(authorizationRequest.getMTI(), "0110");
					Assert.assertEquals(authorizationRequest.getValue(39), "00");

					log.debug("Answer : " + res.getData());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST_NETWORK");
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}

		});

		Context.getInstance().addStartPoint(new Date(), "TEST_NETWORK");

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
