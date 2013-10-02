package model.strategies;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;
import simulator.Utils;
import model.component.Component;
import model.component.ComponentIO;
import model.mediator.HalfDuplexMediator;
import model.mediator.Mediator;

public class CardStrategy implements IStrategy {

	private static Logger log = LoggerFactory.getLogger(CardStrategy.class);
	
	private ComponentIO card;
	
	
	public CardStrategy() {
		// TODO Auto-generated constructor stub
	}

	public CardStrategy(ComponentIO card2) {
		card = card2;
	}

	public Component getCard() {
		return card;
	}

	public void setCard(ComponentIO card) {
		this.card = card;
	}


	@Override
	public void inputTreatment(Mediator m, String data) {
		HashMap<String, String> d = Utils.string2Hashmap(data);
		int montant = Integer.parseInt(d.get("debit"));
		log.info("tpe demande un debit de " + montant);
		//j'ai une co en half-duplex...je peux repondre directement
		if (m instanceof HalfDuplexMediator){
			if (montant>1000){
				card.output(m, "demande auth pour le debit");
			}else{
				card.output(m, "demande de saisie PIN");
			}
		}	
		
		
	}

	@Override
	public void outputTreatment(Mediator m, String data) {
		// TODO Auto-generated method stub
		
	}
}