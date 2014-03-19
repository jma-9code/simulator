package fr.ensicaen.simulator.model.response;

import fr.ensicaen.simulator.model.mediator.Mediator;

/**
 * Wrapper de réponse contenant - Données de réponse - Mediateur utilisé pour
 * transiter la réponse
 * 
 * Note : Si un nouvel échange est réalisé avec la même destination, ce
 * médiateur peut être ré-utilisé compte tenu qu'il a potentiellement été routé.
 * 
 * @author Flo
 */
public class DataResponse implements IResponse {

	private Mediator mediator;
	private String data;

	private DataResponse(Mediator mediator, String data) {
		this.mediator = mediator;
		this.data = data;
	}

	public Mediator getMediator() {
		return this.mediator;
	}

	public String getData() {
		return this.data;
	}

	public static DataResponse build(Mediator mediator, String data) {
		return new DataResponse(mediator, data);
	}

	public static DataResponse build(Mediator mediator, byte[] data) {
		return new DataResponse(mediator, new String(data));
	}

	@Override
	public boolean isVoid() {
		return false;
	}

}
