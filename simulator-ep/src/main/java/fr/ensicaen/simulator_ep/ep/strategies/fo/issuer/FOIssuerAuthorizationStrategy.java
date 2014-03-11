package fr.ensicaen.simulator_ep.ep.strategies.fo.issuer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.DataResponse;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.tools.LogUtils;
import fr.ensicaen.simulator_ep.utils.ISO7816Tools;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOIssuerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOIssuerAuthorizationStrategy.class);

	public FOIssuerAuthorizationStrategy() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		List<PropertyDefinition> propDefs = new ArrayList<PropertyDefinition>();
		propDefs.add(new PropertyDefinition("acceptance", null, true,
				"Stratégie d'approbation des autorisations (0 = OK, 1 = KO, 2 = Random)"));
		return propDefs;
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO frontOfficeIssuer, Mediator m, String data) {
		ISOMsg authorizationAnswer = null;
		Random r = new Random();
		try {
			log.debug(LogUtils.MARKER_COMPONENT_INFO, "FO Issuer authorization receive the ARQC from the FO acquierer");
			authorizationAnswer = ISO8583Tools.read(data);
			authorizationAnswer.setMTI("0110");
			authorizationAnswer.set(7, ISO7816Tools.writeDATETIME(Context.getInstance().getTime()));
			// FO utilisation du champs acceptance afin de definir la strategie
			// d'approbation ou non de l'auth
			int auth_approval = Integer.parseInt(frontOfficeIssuer.getProperties().get("acceptance"));
			String approval_code = new BigInteger(20, r).toString();
			switch (auth_approval) {
				case 0:
					authorizationAnswer.set(39, "00");// Response Code
					authorizationAnswer.set(38, approval_code); // Approval Code
					break;
				case 1:
					authorizationAnswer.set(39, "01");// Response Code
					break;
				case 2:
					if (r.nextBoolean()) {
						authorizationAnswer.set(39, "00");// Response Code
						authorizationAnswer.set(38, approval_code); // Approval
																	// Code
					}
					else {
						authorizationAnswer.set(39, "01");// Response Code
					}
					break;
				default:
					authorizationAnswer.set(39, "01");// Response Code
					break;
			}

		}
		catch (ISOException | ISO8583Exception e) {
			e.printStackTrace();
		}

		try {
			log.debug(LogUtils.MARKER_COMPONENT_INFO, "FO Issuer authorization send the ARPC to the FO acquierer");
			return DataResponse.build(m, new String(authorizationAnswer.pack()));
		}
		catch (ISOException e) {
			e.printStackTrace();
			return VoidResponse.build();
		}
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FO/Issuer/Authorization";
	}

}
