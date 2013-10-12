package model.response;

import model.mediator.Mediator;

public class VoidResponse implements IResponse {
	
	public static VoidResponse build() {
		return new VoidResponse();
	}
	
}
