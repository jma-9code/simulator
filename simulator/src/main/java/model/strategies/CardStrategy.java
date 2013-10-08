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

	private boolean cmdOK(HashMap<String, String> d){
		if (d.get("content-type") != null){
			
			return d.get("content-type").equalsIgnoreCase("iso7816");
		}
		return false;
	}

	@Override
	public void process(Mediator m, String data) {
		HashMap<String, String> d = Utils.string2Hashmap(data);
		
		if (!cmdOK(d)){
			log.warn(card.getName() + " impossible de gerer la donnee");
			return;
		}
		//tpe.output(m, "content-type:iso7816;type:rq;msg:initco;protocols:B0',CB2A;ciphersetting:none,RSA2048")
		
		switch(d.get("msg")){
			case "initco":
				String protocol = card.getProperties().get("protocol");
				String cipher = card.getProperties().get("cipher");
				
				if (d.get("protocols").contains(protocol)){
					//cool ils peuvent communiquer entre eux	
					
				}
				
				if (d.get("ciphersetting").contains(cipher)){
					//cool ils ont le meme cipher de dispo
				}
				
				
				
				String outputdata = "content-type:iso7816;type:rp;msg:initco;protocol:"+protocol+";ciphersetting:"+cipher;
				card.output(m, outputdata);
				break;
			case "pin":
				
				break;
			case "arpc":
				
				break;		
		}
		
		
		/*int montant = Integer.parseInt(d.get("debit"));
		log.info("tpe demande un debit de " + montant);
		//j'ai une co en half-duplex...je peux repondre directement
		if (m instanceof HalfDuplexMediator){
			if (montant>1000){
				card.output(m, "demande auth pour le debit");
			}else{
				card.output(m, "demande de saisie PIN");
			}
		}	*/
		
		
	}

}