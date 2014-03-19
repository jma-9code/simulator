package fr.ensicaen.simulator_ep.ep.strategies.fo.acquirer;

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
import fr.ensicaen.simulator_ep.utils.ISO8583Exception;
import fr.ensicaen.simulator_ep.utils.ISO8583Tools;

public class FOAcquirerRemoteDataCollectionStrategy implements IStrategy<ComponentIO> {

	public static final String CKEY_NB_MESSAGE = "nb_message";
	public static final String CKEY_CURRENT_MESSAGE = "current_message";
	public static final String CKEY_TOTAL_AMOUNT = "total_amount";
	private static Logger log = LoggerFactory.getLogger(FOAcquirerRemoteDataCollectionStrategy.class);

	@Override
	public List<PropertyDefinition> getPropertyDefinitions() {
		return null;
	}

	@Override
	public void init(IOutput _this, Context ctx) {
	}

	@Override
	public void processEvent(ComponentIO _this, String event) {
	}

	@Override
	public IResponse processMessage(ComponentIO _this, Mediator m, String data) {

		try {
			ISOMsg input = ISO8583Tools.read_CB2A_TLC(data);
			ISOMsg output = null;

			switch (input.getMTI()) {
				case "0804":
					output = manageSignOn(input);
					break;

				case "0306":
					output = manageSubmissionHeader(_this, input);
					break;

				case "0246":
					output = manageFinancialTransaction(_this, input);
					break;

				case "0506":
					output = manageFinancialConsolidation(_this, input);
					break;

				case "0844":
					// droit de parole
					if ("851".equals(input.getString(24))) {
						output = manageRightToSpeak(input);
					}
					// notification de déconnexion
					else if ("860".equals(input.getString(24))) {
						output = manageDecoNotification(input);
					}
					break;

				default:
					log.error("MTI " + input.getMTI() + " is not managed");
					break;
			}

			// cas 0246
			if (output == null) {
				return VoidResponse.build();
			}
			else {
				return DataResponse.build(m, new String(output.pack()));
			}
		}
		catch (ISO8583Exception | ISOException e) {
			e.printStackTrace();
			return VoidResponse.build();
		}
	}

	/**
	 * Gestion d'un message de notification de déconnexion
	 * 
	 * @param input
	 * @return Message réponse
	 */
	private ISOMsg manageDecoNotification(ISOMsg input) {
		ISOMsg output = (ISOMsg) input.clone();

		try {
			output.setMTI("0854");
			output.set(39, "0000"); // code action : refused
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * Gestion d'un message de droit de parole
	 * 
	 * @param input
	 * @return Message réponse négatif
	 */
	private ISOMsg manageRightToSpeak(ISOMsg input) {
		ISOMsg output = (ISOMsg) input.clone();

		try {
			output.setMTI("0854");
			output.set(39, "1000"); // code action : refused
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * Gestion d'un message de consolidation (0506)
	 * 
	 * @param _this
	 * @param input
	 * @return Message réponse (0516)
	 */
	private ISOMsg manageFinancialConsolidation(ComponentIO _this, ISOMsg input) {
		int total_amount = 0;

		// traitement input
		try {
			total_amount = Integer.parseInt(input.getString(86));
		}
		catch (NumberFormatException e) {
			log.error("Field 86 invalid.");
		}

		// test de cohérence sans impact
		if (total_amount != _this.getProperties().getInt(CKEY_TOTAL_AMOUNT)) {
			log.error("Total amounts are different");
			// need implementation
		}

		// construction réponse
		ISOMsg output = ISO8583Tools.create_CB2A_TLC();

		try {
			output.setMTI("0516");
			output.set(70, "00000200000199"); // gestion du transfert
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * Gestion d'un message d'initialisation de transfert remise (0306)
	 * 
	 * @param _this
	 * @param input
	 * @return Message réponse (0316)
	 */
	private ISOMsg manageSubmissionHeader(ComponentIO _this, ISOMsg input) {

		// n4 : identifiant du fichier
		// n4 : nombre de message
		// n2 : fenetre d'acquittement
		String f70 = input.getString(70);

		// init nb message 0246 attendu
		_this.getProperties().put(CKEY_NB_MESSAGE, f70.substring(6, 12));
		_this.getProperties().put(CKEY_CURRENT_MESSAGE, "0");

		ISOMsg output = (ISOMsg) input.clone();

		try {
			output.unset(12);
			output.unset(13);
			output.unset(18);
			output.setMTI("0316");
			output.set(39, "0000"); // code action : approved.
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * Gestion d'un message de Sign-On (0804)
	 * 
	 * @param input
	 * @return Message réponse (0814)
	 */
	private ISOMsg manageSignOn(ISOMsg input) {
		ISOMsg output = (ISOMsg) input.clone();

		try {
			output.unset(25);
			output.unset(41);
			output.unset(42);
			output.setMTI("0814");
			output.set(39, "0000"); // code action : approved.
			output.set(67, "0100"); // gestion des transferts
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * Gestion d'un message de transaction financière (0246)
	 * 
	 * @param _this
	 * @param input
	 * @return null ou Message réponse (0256)
	 */
	private ISOMsg manageFinancialTransaction(ComponentIO _this, ISOMsg input) {
		ISOMsg output = null;
		// input.dump(System.out, "\t");

		try {
			// traitement de la transaction
			long amount = Long.parseLong(input.getString(4));
			long totalAmount = _this.getProperties().getLong(CKEY_TOTAL_AMOUNT) + amount;
			_this.getProperties().put(CKEY_TOTAL_AMOUNT, totalAmount);
		}
		catch (NumberFormatException e) {
			log.warn("Invalid amount in the financial transaction message");
			// erreur non gérée
			return null;
		}

		// ack ou pas ?
		int currentMessage = _this.getProperties().getInt(CKEY_CURRENT_MESSAGE) + 1;
		_this.getProperties().put(CKEY_CURRENT_MESSAGE, currentMessage);
		if (currentMessage >= _this.getProperties().getInt(CKEY_NB_MESSAGE)) {

			try {
				output = ISO8583Tools.create_CB2A_TLC();
				output.setMTI("0256");
				output.set("26", "000002");
			}
			catch (ISOException e) {
				e.printStackTrace();
			}

		}

		return output;
	}

	@Override
	public String toString() {
		return "FO/Acquirer/Remote Data Collection";
	}
}
