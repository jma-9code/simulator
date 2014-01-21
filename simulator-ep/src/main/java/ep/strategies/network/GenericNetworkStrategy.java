package ep.strategies.network;

import model.component.ComponentIO;
import model.component.IOutput;
import model.mediator.Mediator;
import model.response.DataResponse;
import model.response.IResponse;
import model.response.VoidResponse;
import model.strategies.IStrategy;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.Context;
import simulator.exception.ContextException;
import utils.CB2AValues;

public class GenericNetworkStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(GenericNetworkStrategy.class);

	public final static String CKEY_NAME = "name";
	public final static String CKEYPREFIX_ISSUER_OF = "issuer_of_";

	public final static String MKEY_ISSUER_ID = "issuer_id";

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
		ISOMsg input = new ISOMsg();

		try {
			input.setPackager(new GenericPackager("8583.xml"));
			input.unpack(data.getBytes());

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
							Mediator mediatorToIssuer = ctx.getFirstMediator(_this, "Issuer Authorization Module",
									MKEY_ISSUER_ID, issuerId);

							if (mediatorToIssuer != null) {
								// The server response check is not implemented
								return mediatorToIssuer.send(_this, data);
							}
							else {
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
		catch (ISOException e) {
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
}