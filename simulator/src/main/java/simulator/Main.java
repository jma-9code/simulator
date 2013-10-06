package simulator;


import model.component.Component;
import model.component.ComponentI;
import model.component.ComponentIO;
import model.component.ComponentO;
import model.mediator.HalfDuplexMediator;
import model.strategies.CardStrategy;
import model.strategies.TPEStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		log.info("Creation d'un porteur");
		Component florent = new ComponentIO("personne");
		florent.getProperties().put("nom", "moisson");
		florent.getProperties().put("prenom", "florent");
		florent.getProperties().put("age", "22");
		florent.getProperties().put("adresse", "...........");
		log.info(florent.toString());
		
		log.info("Creation d'une banque ac 1 compte");
		Component bank = new ComponentIO("banque");
		bank.getProperties().put("marque", "bnp");
		Component account = new ComponentIO("compte");
		account.getProperties().put("porteur", "florent moisson");
		account.getProperties().put("montant", "1500");
		account.getProperties().put("plafond", "9000");
		bank.getComponents().add(account);
		log.info(bank.toString());
		
		log.info("Creation d'une carte bancaire");
		ComponentIO card = new ComponentIO("cb");
		Component chip = new ComponentIO("puce");
		Component magstrippe = new ComponentIO("piste magnetique");
		chip.getProperties().put("pan", "1111111111111111111111111");
		chip.getProperties().put("bccs", "12421874");
		card.getProperties().put("pan", "1111111111111111111111111");
		card.getProperties().put("icvv", "000");
		card.getProperties().put("genre", "M");
		card.getProperties().put("nom porteur", "Florent Moisson");
		card.getProperties().put("date expiration", "09/15");
		magstrippe.getProperties().put("piste iso2", "59859595985888648468454684");
		card.getComponents().add(magstrippe);
		card.getComponents().add(chip);
		log.info(card.toString());
		//Comportement de la carte
		card.setStrategy(new CardStrategy(card));
		
		
		log.info("Creation d'un TPE");
		ComponentIO tpe = new ComponentIO("tpe");
		Component magstrippeReader = new ComponentI("lecteur piste magnetique");
		Component pinpad = new ComponentI("pinpad");
		Component chipReader = new ComponentIO("lecteur carte a puce");
		ComponentIO core = new ComponentIO("logiciel integre");
		core.getProperties().put("banque acquereur", "bnp");
		tpe.getComponents().add(magstrippeReader);
		tpe.getComponents().add(pinpad);
		tpe.getComponents().add(chipReader);
		tpe.getComponents().add(core);
		log.info(tpe.toString());
		//Comportement de la tpe
		tpe.setStrategy(new TPEStrategy(tpe));
		
		HalfDuplexMediator hfd = new HalfDuplexMediator(card, tpe);
		
		AsyncSimulator simulator = SimulatorFactory.getAsyncSimulator();
		simulator.start();
		
		//tpe.output(hfd, "debit:1000;commercant:blbla"); 
		
	}

}
