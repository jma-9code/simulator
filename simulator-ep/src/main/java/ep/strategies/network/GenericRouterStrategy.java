package ep.strategies.network;

import model.component.ComponentIO;
import model.component.IOutput;
import model.mediator.Mediator;
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

/**
 * Generic component allows to route a CB2A message to the good network. This
 * component represents a logical layer that is implemented in the acquirer
 * authorization module. This component does not modify the message, so just the
 * good case is manage.
 */
public class GenericRouterStrategy implements IStrategy<ComponentIO> {

	private static Logger log = LoggerFactory.getLogger(GenericRouterStrategy.class);

	public final static String CKEYPREFIX_NETWORK_OF = "network_of_";
	public final static String MKEY_NETWORK_ID = "network_id";

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator mediator, String data) {
		log.info("Message received on network router");

		// message 8583
		ISOMsg input = new ISOMsg();

		try {
			input.setPackager(new GenericPackager("8583.xml"));
			input.unpack(data.getBytes());

			// PAN available ?
			if (input.hasField(2)) {
				String pan = (String) input.getValue(2);

				// check pan format
				if (pan != null && pan.length() > 4) {
					// formerly named "BIN"
					String iin = pan.substring(0, 4);
					log.info("Search network for iin = " + iin);

					// router from 1 to 3 figures
					int n = 5;
					String networkId = null;
					while (networkId == null && n-- > 1) {
						String prefix = iin.substring(0, n);
						log.debug("Check if network routing exists for prefix " + prefix);
						networkId = _this.getProperties().get(CKEYPREFIX_NETWORK_OF + prefix);
					}

					if (networkId != null) {
						log.debug("Network found : " + networkId);

						try {
							// considering that networkId is like an ip address
							// to reach the network. On our model, the mediator
							// represents the support, then it also contains
							// this
							// identifiant.
							Context ctx = Context.getInstance();
							Mediator mediatorToNetwork = ctx.getFirstMediator(_this, "Network", MKEY_NETWORK_ID,
									networkId);

							if (mediatorToNetwork != null) {
								// The server response check is not implemented
								return mediatorToNetwork.send(_this, data);
							}
							else {
								log.error("No mediator found.");
							}

						}
						catch (ContextException e) {
							log.error("Context exception catched, will answer card issuer is unreachable.", e);
							e.printStackTrace();
						}
					}
				}
				else {
					log.error("Field 39 (PAN) is empty");
				}
			}
			else {
				log.error("Field 39 (PAN) does not exist");
			}
		}
		catch (ISOException e) {
			log.error("Exception while unpacking message", e);

			// anormal
			return VoidResponse.build();
		}

		// anormal
		return VoidResponse.build();
	}
}