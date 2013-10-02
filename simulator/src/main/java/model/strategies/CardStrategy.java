package model.strategies;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Main;
import simulator.Utils;
import model.component.Component;
import model.component.ComponentIO;

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
	public void outputTreatment(Component c, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String inputTreatment(Component c, String data) {
		HashMap<String, String> d = Utils.string2Hashmap(data);
		switch(c.getName()){
			case "tpe":
				int montant = Integer.parseInt(d.get("debit"));
				log.info("tpe demande un debit de " + montant);
				if (montant>1000){
					card.output(c, "demande auth pour le debit");
				}else{
					card.output(c, "demande de saisie PIN");
				}
				break;
			default:
				break;
		}
		return null;
	}

}
