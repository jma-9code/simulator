package ept;

import java.util.Date;

import model.component.ComponentIO;
import model.component.IOutput;
import model.factory.MediatorFactory;
import model.factory.MediatorFactory.EMediator;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.strategies.IStrategy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simulator.Context;
import simulator.SimulatorFactory;
import simulator.exception.SimulatorException;
import ep.strategies.ept.EPTChipsetStrategy;
import ep.strategies.ept.EPTSmartCardReader;
import ep.strategies.ept.EPTStrategy;

/**
 * Electronic Payment Terminal (EPT)
 * 
 * Terminal de Paiement Electronique |- Chipset |- Lecteur carte à piste |-
 * Lecteur carte à puce |- Lecteur carte NFC [*] |- Emplacement SAM |-
 * Emplacement SIM [*] |- Imprimante |- Pinpad sécurisé |- Interface réseau
 * acquéreur (Modem, Ethernet, GPRS, ...) |- Interface caisse (Serial, Ethernet,
 * ...) [*]
 * 
 * [*] : facultatif
 * 
 * Source : http://fr.wikipedia.org/wiki/Terminal_de_paiement_%C3%A9lectronique
 * 
 * @author Flo
 */
public class EPTUnitTest {

	private static ComponentIO fakeSmartCard;

	private static ComponentIO ept;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;

	@Before
	public void init() throws Exception {
		MediatorFactory factory = MediatorFactory.getInstance();

		Context.getInstance().autoRegistrationMode();
		fakeSmartCard = new ComponentIO("Smart Card");

		ept = new ComponentIO("Electronic Payment Terminal");
		ept.setStrategy(new EPTStrategy());

		smartCardReader = new ComponentIO("Smart Card Reader");
		smartCardReader.setStrategy(new EPTSmartCardReader());
		ept.getComponents().add(smartCardReader);
		factory.getMediator(ept, smartCardReader, EMediator.HALFDUPLEX);

		chipset = new ComponentIO("Chipset");
		chipset.setStrategy(new EPTChipsetStrategy());
		chipset.getProperties().put("pos_id", "0000623598");
		chipset.getProperties().put("stan", "000001");
		chipset.getProperties().put("protocol_list", "ISO7816 ISO8583 CB2A-T");
		chipset.getProperties().put("protocol_prefered", "ISO7816");
		ept.getComponents().add(chipset);
		factory.getMediator(ept, chipset, EMediator.HALFDUPLEX);

		printer = new ComponentIO("Printer");
		ept.getComponents().add(printer);

		securePinPad = new ComponentIO("Secure pin pad");
		ept.getComponents().add(securePinPad);

		networkInterface = new ComponentIO("Network interface");
		ept.getComponents().add(networkInterface);

		// static mediators
		factory.getMediator(ept, fakeSmartCard, EMediator.SIMPLEX);
	}

	@After
	public void clean() throws Exception {
		Context.getInstance().reset();
	}

	@Test
	public void testPipedMediator() {
		fakeSmartCard.setStrategy(new IStrategy<ComponentIO>() {

			@Override
			public void init(Context ctx) {
			}

			@Override
			public void processEvent(ComponentIO _this, String event) {
			}

			@Override
			public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
				return DataResponse.build(mediator, "TEST ANSWER CARD");
			}

		});
		chipset.notifyEvent("SMART_CARD_INSERTED");

	}

	public void simulate(IOutput dst, String event) throws SimulatorException {
		// add start point for the simulator
		Context.getInstance().addStartPoint(new Date(), dst, event);

		// execute simulation.
		SimulatorFactory.getSimulator().start();
	}
}
