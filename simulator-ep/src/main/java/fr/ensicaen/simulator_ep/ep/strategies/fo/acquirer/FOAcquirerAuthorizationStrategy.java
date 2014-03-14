package fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator.tools.LogUtils;
import fr.ensicaen.simulator_ep.utils.ComponentEP;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOAcquirerAuthorizationStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(FOAcquirerAuthorizationStrategy.class);

	public final static String CKEY_IIN_ON_US = "iin_on_us";

	public FOAcquirerAuthorizationStrategy() {
		super();
	}

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator m, String data) {

		// c'est chinois ou chez moi ?
		boolean on_us = false;

		// récupération des bin de chez nous depuis le paramètrage
		String iin_on_us = _this.getProperty(CKEY_IIN_ON_US);

		// init
		String auth_iin = null;

		try {
			ISOMsg input = ISO8583Tools.read(data);
			String pan = (String) input.getValue(2);

			// formerly named "BIN"
			if (pan != null && pan.length() > 6) {
				auth_iin = pan.substring(0, 6);
			}
		}
		catch (ISO8583Exception | ISOException e) {
			log.error("Error while getting iin", e);
			return VoidResponse.build();
		}

		log.debug("Authorization for IIN " + auth_iin + "...");

		// iin on us à traiter
		if (iin_on_us != null && !iin_on_us.trim().isEmpty()) {
			StringTokenizer token = new StringTokenizer(iin_on_us, ";");
			while (token.hasMoreTokens()) {
				String iin = token.nextToken();

				// iin = auth_iin ?
				if (iin != null && iin.equals(auth_iin)) {
					on_us = true;
					break;
				}
			}
		}

		try {
			// on_us
			if (on_us) {
				Mediator mIssuerModule = Context.getInstance().getFirstMediator(_this,
						ComponentEP.FO_ISSUER_AUTHORIZATION.ordinal());
				log.debug(LogUtils.MARKER_COMPONENT_INFO,
						"FO Acquirer authorization module send the msg to the FO issuer");
				return mIssuerModule.send(_this, data);
			}
			// off_us
			else {
				Mediator mRouter = Context.getInstance().getFirstMediator(_this, ComponentEP.ROUTER.ordinal());
				log.debug(LogUtils.MARKER_COMPONENT_INFO, "FO Acquirer authorization module send the msg to the router");
				return mRouter.send(_this, data);
			}
		}
		catch (ContextException e) {
			return VoidResponse.build();
		}
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {

	}

	@Override
	public String toString() {
		return "FO/Acquirer/Authorization";
	}

}
