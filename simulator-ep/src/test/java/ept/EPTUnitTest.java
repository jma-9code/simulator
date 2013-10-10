package ept;

import model.component.ComponentI;
import model.component.ComponentIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ep.strategies.ept.EPTChipsetStrategy;
import ep.strategies.ept.EPTStrategy;

/**
 * Electronic Payment Terminal (EPT)
 * 
 * 	  Terminal de Paiement Electronique
 *  	|- Chipset
 *  	|- Lecteur carte à piste
 *  	|- Lecteur carte à puce
 *  	|- Lecteur carte NFC [*]
 *  	|- Emplacement SAM
 *  	|- Emplacement SIM [*]
 *  	|- Imprimante
 *  	|- Pinpad sécurisé
 *  	|- Interface réseau acquéreur (Modem, Ethernet, GPRS, ...)
 *  	|- Interface caisse (Serial, Ethernet, ...) [*]
 * 
 * 	  [*] : facultatif
 * 
 * Source : http://fr.wikipedia.org/wiki/Terminal_de_paiement_%C3%A9lectronique
 * 
 * @author Flo
 */
public class EPTUnitTest {
	
	private static ComponentIO paymentTerminal;
	private static ComponentIO smartCardReader;
	private static ComponentIO chipset;
	private static ComponentIO printer;
	private static ComponentIO securePinPad;
	private static ComponentIO networkInterface;

	@Before
	public void init() throws Exception {
		paymentTerminal = new ComponentIO("Payment terminal");
		paymentTerminal.setStrategy(new EPTStrategy());
		
		smartCardReader = new ComponentIO("Smart card reader");
		paymentTerminal.getComponents().add(smartCardReader);
		
		chipset = new ComponentIO("Chipset");
		chipset.setStrategy(new EPTChipsetStrategy());
		paymentTerminal.getComponents().add(chipset);
		
		printer = new ComponentIO("Printer");
		paymentTerminal.getComponents().add(printer);
		
		securePinPad = new ComponentIO("Secure pin pad");
		paymentTerminal.getComponents().add(securePinPad);
		
		networkInterface = new ComponentIO("Network interface");
		paymentTerminal.getComponents().add(networkInterface);
	}

	@After
	public void clean() throws Exception {
	}

	@Test
	public void SecureChanneltest() {
	}
}
