package fr.ensicaen.simulator_ep.ep.strategies.network;

import java.util.ArrayList;
import java.util.List;

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
import fr.ensicaen.simulator.simulator.exception.ContextException;
import fr.ensicaen.simulator_ep.utils.CB2AValues;
import fr.ensicaen.simulator_ep.utils.ComponentEP;
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;
import fr.ensicaen.simulator_ep.utils.ProtocolEP;

public class GenericNetworkStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(GenericNetworkStrategy.class);

	public final static String CKEY_NAME = "name";
	public final static String CKEYPREFIX_ISSUER_OF = "issuer_of_";

	public final static String MKEY_ISSUER_ID = "issuer_id";

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return new ArrayList<PropertyDefinition>();
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	/**
	 * Auth Sign-on and Sign-off, Test echo not implemented.
	 */
	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		log.info("Message received on network " + _this.getProperty(CKEY_NAME));

		// message 8583
		ISOMsg input = null;

		try {
			input = ISO8583Tools.read(data);
			if (!input.isRequest()) {
				log.warn("Message is not a request");
			}

			// maybe check MTI in the future ?
			if (input.getMTI().equals("0100")) {
				log.debug("Message is an authorization request");
			}

			// PAN available ?
			if (input.hasField(2)) {
				String pan = (String) input.getValue(2);

				// check pan format
				if (pan != null && pan.length() > 6) {
					// formerly named "BIN"
					String iin = pan.substring(0, 6);
					log.info("Search issuer for iin = " + iin);

					// check if issuer known
					String issuerId = _this.getProperties().get(CKEYPREFIX_ISSUER_OF + iin);
					if (issuerId != null) {
						try {
							// considering that issuerId is like an ip address
							// to reach the FO server of the issuer. On our
							// model, the mediator represents the support, then
							// it also contains this identifiant.
							Context ctx = Context.getInstance();
							Mediator mediatorToIssuer = ctx.getFirstMediator(_this,
									ComponentEP.FO_ISSUER_AUTHORIZATION.ordinal(), MKEY_ISSUER_ID, issuerId);
							mediatorToIssuer.setProtocol(ProtocolEP.ISO8583.toString());
							if (mediatorToIssuer != null) {
								// The server response check is not implemented
								return mediatorToIssuer.send(_this, data);
							}
							else {
								log.warn("No mediator found for issuer id : " + issuerId);

								// answer that card issuer is unreachable
								input.setResponseMTI();
								input.set(39, CB2AValues.Field39.UNREACHABLE_CARD_ISSUER);
								return DataResponse.build(mediator, input.pack());
							}

						}
						catch (ContextException e) {
							log.error("Context exception catched, will answer card issuer is unreachable.", e);
							e.printStackTrace();

							// answer that card issuer is unreachable
							input.setResponseMTI();
							input.set(39, CB2AValues.Field39.UNREACHABLE_CARD_ISSUER);
							return DataResponse.build(mediator, input.pack());
						}
					}
					else {
						// answer that card issuer is unknown
						input.setResponseMTI();
						input.set(39, CB2AValues.Field39.UNKNOWN_CARD_ISSUER);
						return DataResponse.build(mediator, input.pack());
					}

				}
				else {
					// answer that message format is invalid
					input.setResponseMTI();
					input.set(39, CB2AValues.Field39.INVALID_FORMAT);
					return DataResponse.build(mediator, input.pack());
				}
			}
			else {
				// answer that message format is invalid
				input.setResponseMTI();
				input.set(39, CB2AValues.Field39.INVALID_FORMAT);
				return DataResponse.build(mediator, input.pack());
			}

		}
		catch (ISOException | ISO8583Exception e) {
			log.error("Exception while unpacking message", e);

			try {
				// answer that message format is invalid
				input.setResponseMTI();
				input.set(39, CB2AValues.Field39.INVALID_FORMAT);
				return DataResponse.build(mediator, data);
			}
			catch (ISOException e1) {
				// anormal
				return VoidResponse.build();
			}
		}
	}

	@Override
	public String toString() {
		return "Generic Network";
	}
}
